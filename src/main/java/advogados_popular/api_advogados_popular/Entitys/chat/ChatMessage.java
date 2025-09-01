package advogados_popular.api_advogados_popular.Entitys.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "chat_messages", indexes = {
        @Index(name="idx_messages_room_created", columnList = "room_id,created_at")
})
@Getter @Setter @NoArgsConstructor
public class ChatMessage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name="fk_message_room"))
    private ChatRoom room;

    @Column(name = "author_type", nullable = false, length = 10)
    private String authorType; // 'user' | 'lawyer'

    @Column(name = "author_id", nullable = false)
    private Long authorId;

    @Column(name = "target_type", nullable = false, length = 10)
    private String targetType; // 'user' | 'lawyer'

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "text", nullable = false, length = 2000)
    private String text;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();
}

