package advogados_popular.api_advogados_popular.Repositorys.chat;

import advogados_popular.api_advogados_popular.Entitys.chat.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByRoomKey(String roomKey);
}

