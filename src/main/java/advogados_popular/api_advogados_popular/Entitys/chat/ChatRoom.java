package advogados_popular.api_advogados_popular.Entitys.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "chat_rooms", indexes = { @Index(name="uk_chat_room_key", columnList = "room_key", unique = true) })
@Getter @Setter @NoArgsConstructor
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_key", nullable = false, unique = true)
    private String roomKey;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}

