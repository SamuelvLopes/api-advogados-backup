package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.Entitys.chat.ChatMessage;
import advogados_popular.api_advogados_popular.Entitys.chat.ChatRoom;
import advogados_popular.api_advogados_popular.Entitys.chat.ChatRoomMember;
import advogados_popular.api_advogados_popular.Repositorys.chat.ChatMessageRepository;
import advogados_popular.api_advogados_popular.Repositorys.chat.ChatRoomMemberRepository;
import advogados_popular.api_advogados_popular.Repositorys.chat.ChatRoomRepository;
import advogados_popular.api_advogados_popular.Repositorys.UserRepository;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.Utils.chat.RoomNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;

@Service
public class ChatService {
    private final ChatRoomRepository roomRepo;
    private final ChatRoomMemberRepository memberRepo;
    private final ChatMessageRepository msgRepo;
    private final UserRepository userRepo;
    private final AdvogadoRepository advRepo;

    public ChatService(ChatRoomRepository roomRepo, ChatRoomMemberRepository memberRepo, ChatMessageRepository msgRepo,
                       UserRepository userRepo, AdvogadoRepository advRepo) {
        this.roomRepo = roomRepo;
        this.memberRepo = memberRepo;
        this.msgRepo = msgRepo;
        this.userRepo = userRepo;
        this.advRepo = advRepo;
    }

    public String normalize(String room) {
        return RoomNormalizer.normalize(room);
    }

    @Transactional
    public ChatRoom ensureRoom(String normalizedKey) {
        return roomRepo.findByRoomKey(normalizedKey).orElseGet(() -> {
            ChatRoom r = new ChatRoom();
            r.setRoomKey(normalizedKey);
            return roomRepo.save(r);
        });
    }

    @Transactional
    public ChatRoomMember ensureMember(ChatRoom room, String actorType, Long actorId) {
        return memberRepo.findByRoomAndActorTypeAndActorId(room, actorType, actorId)
                .orElseGet(() -> {
                    ChatRoomMember m = new ChatRoomMember();
                    m.setRoom(room);
                    m.setActorType(actorType);
                    m.setActorId(actorId);
                    m.setLastSeenAt(Instant.EPOCH);
                    return memberRepo.save(m);
                });
    }

    @Transactional
    public ChatMessage postMessage(String roomKey, String authorType, Long authorId, String targetType, Long targetId, String text) {
        ChatRoom room = ensureRoom(roomKey);
        ensureMember(room, authorType, authorId);
        ensureMember(room, targetType, targetId);
        ChatMessage m = new ChatMessage();
        m.setRoom(room);
        m.setAuthorType(authorType);
        m.setAuthorId(authorId);
        m.setTargetType(targetType);
        m.setTargetId(targetId);
        m.setText(text);
        m.setCreatedAt(Instant.now());
        return msgRepo.save(m);
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> getMessages(String roomKey, Long meId, Integer limit, Instant before) {
        ChatRoom room = roomRepo.findByRoomKey(roomKey).orElse(null);
        if (room == null) return Collections.emptyList();
        List<ChatMessage> list = (before != null) ? msgRepo.findByRoomAndCreatedAtBeforeOrderByCreatedAtAsc(room, before)
                : msgRepo.findByRoomOrderByCreatedAtAsc(room);
        if (limit != null && limit > 0 && list.size() > limit) {
            list = list.subList(list.size() - limit, list.size());
        }
        Instant lastSeen = null;
        if (meId != null) {
            Optional<ChatRoomMember> opt = memberRepo.findByRoomAndActorTypeAndActorId(room, "user", meId);
            if (opt.isEmpty()) opt = memberRepo.findByRoomAndActorTypeAndActorId(room, "lawyer", meId);
            lastSeen = opt.map(ChatRoomMember::getLastSeenAt).orElse(Instant.EPOCH);
        }
        List<Map<String,Object>> out = new ArrayList<>();
        for (ChatMessage m : list) {
            boolean read = (lastSeen != null) && !m.getCreatedAt().isAfter(lastSeen);
            Map<String,Object> item = new LinkedHashMap<>();
            item.put("id", m.getId());
            item.put("fromId", m.getAuthorId());
            item.put("author", m.getAuthorType());
            item.put("toId", m.getTargetId());
            item.put("text", m.getText());
            item.put("at", m.getCreatedAt().toString());
            item.put("read", read);
            out.add(item);
        }
        return out;
    }

    @Transactional
    public void open(String meType, Long meId, String otherType, Long otherId) {
        String roomKey = canonicalFromTypes(meType, meId, otherType, otherId);
        ChatRoom room = ensureRoom(roomKey);
        ChatRoomMember me = ensureMember(room, meType, meId);
        ensureMember(room, otherType, otherId);
        me.setLastSeenAt(Instant.now());
        memberRepo.save(me);
    }

    public String canonicalFromTypes(String t1, Long id1, String t2, Long id2) {
        Long uId = null; Long aId = null;
        if ("user".equals(t1)) uId = id1; if ("lawyer".equals(t1)) aId = id1;
        if ("user".equals(t2)) uId = id2; if ("lawyer".equals(t2)) aId = id2;
        if (uId == null || aId == null) throw new IllegalArgumentException("Both user and lawyer ids required");
        return "pair-u" + uId + "-a" + aId;
    }

    @Transactional(readOnly = true)
    public List<Map<String,Object>> getUnreadSummary(Long userId) {
        List<ChatRoomMember> memberships = memberRepo.findByActorId(userId);
        List<Map<String,Object>> out = new ArrayList<>();
        for (ChatRoomMember mbr : memberships) {
            ChatRoom room = mbr.getRoom();
            Instant lastSeen = mbr.getLastSeenAt() != null ? mbr.getLastSeenAt() : Instant.EPOCH;
            // Count messages newer than lastSeen directed to this userId
            List<ChatMessage> msgs = msgRepo.findByRoomOrderByCreatedAtAsc(room);
            long count = msgs.stream().filter(mm -> mm.getTargetId().equals(userId) && mm.getCreatedAt().isAfter(lastSeen)).count();
            if (count > 0) {
                out.add(Map.of("room", room.getRoomKey(), "count", count));
            }
        }
        return out;
    }
}
