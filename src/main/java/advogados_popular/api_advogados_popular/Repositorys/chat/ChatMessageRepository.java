package advogados_popular.api_advogados_popular.Repositorys.chat;

import advogados_popular.api_advogados_popular.Entitys.chat.ChatMessage;
import advogados_popular.api_advogados_popular.Entitys.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByRoomOrderByCreatedAtAsc(ChatRoom room);
    List<ChatMessage> findByRoomAndCreatedAtBeforeOrderByCreatedAtAsc(ChatRoom room, Instant before);
}

