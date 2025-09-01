package advogados_popular.api_advogados_popular.Repositorys.chat;

import advogados_popular.api_advogados_popular.Entitys.chat.ChatRoom;
import advogados_popular.api_advogados_popular.Entitys.chat.ChatRoomMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChatRoomMemberRepository extends JpaRepository<ChatRoomMember, Long> {
    Optional<ChatRoomMember> findByRoomAndActorTypeAndActorId(ChatRoom room, String actorType, Long actorId);
    java.util.List<ChatRoomMember> findByActorId(Long actorId);
}
