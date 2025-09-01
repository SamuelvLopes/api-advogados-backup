package advogados_popular.api_advogados_popular.controllers;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import advogados_popular.api_advogados_popular.sevices.ChatService;
import advogados_popular.api_advogados_popular.Utils.chat.RoomNormalizer;

@RestController
@RequestMapping("/chat")
@CrossOrigin(origins = "*")
public class ChatController {
    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    public record Msg(String id, String fromId, String author, String toId, String text, long at) {}

    private static final Map<String, List<Msg>> rooms = new ConcurrentHashMap<>();
    private static final Map<String, List<SseEmitter>> emittersByRoom = new ConcurrentHashMap<>();
    private static final Map<String, Set<String>> membersByRoom = new ConcurrentHashMap<>(); // room -> users
    private static final Map<String, Map<String, Long>> lastSeen = new ConcurrentHashMap<>(); // room -> (user -> at)

    private static String pairKey(String a, String b) {
        if (a == null) a = "";
        if (b == null) b = "";
        return (a.compareTo(b) <= 0) ? ("u" + a + "-u" + b) : ("u" + b + "-u" + a);
    }

    @GetMapping("/{room}")
    public ResponseEntity<List<Msg>> history(@PathVariable("room") String room) {
        return ResponseEntity.ok(rooms.getOrDefault(room, new ArrayList<>()));
    }

    @PostMapping("/{room}")
    public ResponseEntity<Map<String,Object>> send(@PathVariable("room") String room, @RequestBody Map<String, String> body) {
        String normalized = RoomNormalizer.normalize(room);
        if (normalized == null) return ResponseEntity.badRequest().body(Map.of("message","roomId inválido. Formato esperado: pair-u{userId}-a{lawyerId}"));
        String author = body.getOrDefault("author", "");
        String authorId = body.getOrDefault("authorId", "");
        String targetId = body.getOrDefault("targetId", "");
        String text = body.getOrDefault("text", "");
        if (authorId.isEmpty() || targetId.isEmpty() || text.isEmpty()) return ResponseEntity.badRequest().build();
        Long aId = Long.parseLong(authorId);
        Long tId = Long.parseLong(targetId);
        String aType = normalized.contains("u"+aId) ? "user" : "lawyer";
        String tType = normalized.contains("u"+tId) ? "user" : "lawyer";
        var saved = chatService.postMessage(normalized, aType, aId, tType, tId, text);
        Map<String,Object> out = new LinkedHashMap<>();
        out.put("id", saved.getId());
        out.put("fromId", saved.getAuthorId());
        out.put("author", saved.getAuthorType());
        out.put("toId", saved.getTargetId());
        out.put("text", saved.getText());
        out.put("at", saved.getCreatedAt().toString());
        return ResponseEntity.ok().header("X-Normalized-Room", normalized).body(out);
    }

    // Simple endpoint to create a message with body containing the room id
    @PostMapping("/message")
    public ResponseEntity<Map<String,Object>> createMessage(@RequestBody Map<String, String> body) {
        String room = body.getOrDefault("room", "");
        if (room.isEmpty()) return ResponseEntity.badRequest().build();
        return send(room, body);
    }

    @GetMapping(value = "/stream/{room}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable("room") String room) {
        SseEmitter emitter = new SseEmitter(0L); // no timeout
        emittersByRoom.computeIfAbsent(room, r -> Collections.synchronizedList(new ArrayList<>())).add(emitter);

        emitter.onCompletion(() -> removeEmitter(room, emitter));
        emitter.onTimeout(() -> removeEmitter(room, emitter));
        emitter.onError((ex) -> removeEmitter(room, emitter));

        try {
            emitter.send(SseEmitter.event().name("hello").data("connected"));
        } catch (IOException ignored) {}

        return emitter;
    }

    private void removeEmitter(String room, SseEmitter emitter) {
        List<SseEmitter> list = emittersByRoom.get(room);
        if (list != null) {
            list.remove(emitter);
        }
    }

    // Register a user as a member of a room (for unread tracking)
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String room = body.getOrDefault("room", "");
        String userId = body.getOrDefault("userId", "");
        String user = body.getOrDefault("user", ""); // backward-compat
        String key = !userId.isEmpty() ? userId : user;
        if (room.isEmpty() || key.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "room and userId are required"));
        membersByRoom.computeIfAbsent(room, r -> Collections.synchronizedSet(new HashSet<>())).add(key);
        lastSeen.computeIfAbsent(room, r -> new ConcurrentHashMap<>()).putIfAbsent(key, 0L);
        return ResponseEntity.ok(Map.of("status", "registered"));
    }

    // Return unread messages for a user across registered rooms and mark them as seen
    @GetMapping("/unread")
    public ResponseEntity<List<Map<String, Object>>> unread(@RequestParam(value = "userId", required = false) String userId,
                                                            @RequestParam(value = "user", required = false) String user,
                                                            @RequestParam(value = "me", required = false) String me) {
        String key = (me != null && !me.isEmpty()) ? me : (userId != null && !userId.isEmpty()) ? userId : (user != null ? user : "");
        if (key.isEmpty()) return ResponseEntity.ok(Collections.emptyList());
        Long id;
        try { id = Long.parseLong(key); } catch (Exception e) { return ResponseEntity.ok(Collections.emptyList()); }
        return ResponseEntity.ok(chatService.getUnreadSummary(id));
    }

    // Simplest REST endpoints used by the frontend chat (polling per open dialog)
    @GetMapping("/messages")
    public ResponseEntity<List<Map<String,Object>>> listMessages(@RequestParam("room") String room,
                                                                 @RequestParam(value = "me", required = false) String me,
                                                                 @RequestParam(value = "userId", required = false) String userId) {
        String normalized = RoomNormalizer.normalize(room);
        if (normalized == null) {
            return ResponseEntity.badRequest()
                    .header("X-Normalized-Room", "")
                    .body(java.util.List.of());
        }
        Long meId = null;
        try { meId = (me != null && !me.isEmpty()) ? Long.parseLong(me) : (userId != null ? Long.parseLong(userId) : null); } catch (Exception ignored) {}
        List<Map<String,Object>> out = chatService.getMessages(normalized, meId, null, null);
        return ResponseEntity.ok()
                .header("X-Normalized-Room", normalized)
                .body(out);
    }

    @PostMapping("/messages")
    public ResponseEntity<Map<String,Object>> createMessageBody(@RequestBody Map<String, String> body) {
        String room = body.getOrDefault("room", "");
        String author = body.getOrDefault("author", "");
        String authorId = body.getOrDefault("authorId", "");
        String targetId = body.getOrDefault("targetId", "");
        String text = body.getOrDefault("text", "");
        if (room.isEmpty() || authorId.isEmpty() || targetId.isEmpty() || text.isEmpty()) return ResponseEntity.badRequest().build();
        String normalized = RoomNormalizer.normalize(room);
        if (normalized == null) return ResponseEntity.badRequest().header("X-Normalized-Room", "").body(Map.of("message","roomId inválido. Formato esperado: pair-u{userId}-a{lawyerId}"));
        Long aId = Long.parseLong(authorId);
        Long tId = Long.parseLong(targetId);
        String aType = normalized.contains("u"+aId) ? "user" : "lawyer";
        String tType = normalized.contains("u"+tId) ? "user" : "lawyer";
        var saved = chatService.postMessage(normalized, aType, aId, tType, tId, text);
        Map<String,Object> out = new LinkedHashMap<>();
        out.put("id", saved.getId());
        out.put("fromId", saved.getAuthorId());
        out.put("author", saved.getAuthorType());
        out.put("toId", saved.getTargetId());
        out.put("text", saved.getText());
        out.put("at", saved.getCreatedAt().toString());
        return ResponseEntity.ok().header("X-Normalized-Room", normalized).body(out);
    }

    // mark read when chat opens
    @PostMapping("/open")
    public ResponseEntity<Map<String,Object>> open(@RequestBody Map<String, String> body) {
        String me = body.getOrDefault("me", "");
        String other = body.getOrDefault("other", "");
        if (me.isEmpty() || other.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error","me and other required"));
        Long meId = Long.parseLong(me);
        Long otherId = Long.parseLong(other);
        // heurística: se roomId do front for par u-a, deduz tipos; caso contrário, infere pela presença do sufixo na normalização
        String roomUA = "pair-u"+Math.min(meId,otherId)+"-a"+Math.max(meId,otherId);
        String meType = roomUA.contains("u"+meId) ? "user" : "lawyer";
        String otherType = roomUA.contains("u"+otherId) ? "user" : "lawyer";
        chatService.open(meType, meId, otherType, otherId);
        return ResponseEntity.ok(Map.of("status","ok","room", chatService.canonicalFromTypes(meType, meId, otherType, otherId)));
    }
}
