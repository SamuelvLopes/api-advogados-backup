package advogados_popular.api_advogados_popular.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/chat")
public class ChatController {
    public record Msg(String id, String author, String text, long at) {}

    private static final Map<String, List<Msg>> rooms = new ConcurrentHashMap<>();

    @GetMapping("/{room}")
    public ResponseEntity<List<Msg>> history(@PathVariable("room") String room) {
        return ResponseEntity.ok(rooms.getOrDefault(room, new ArrayList<>()));
    }

    @PostMapping("/{room}")
    public ResponseEntity<Msg> send(@PathVariable("room") String room, @RequestBody Map<String, String> body) {
        String author = body.getOrDefault("author", "An�nimo");
        String text = body.getOrDefault("text", "");
        Msg m = new Msg(UUID.randomUUID().toString(), author, text, System.currentTimeMillis());
        rooms.computeIfAbsent(room, r -> new ArrayList<>()).add(m);
        return ResponseEntity.ok(m);
    }
}
