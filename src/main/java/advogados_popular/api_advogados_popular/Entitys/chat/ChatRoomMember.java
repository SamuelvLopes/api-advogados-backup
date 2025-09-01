package advogados_popular.api_advogados_popular.Entitys.chat;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "chat_room_members",
        uniqueConstraints = @UniqueConstraint(name="uk_member", columnNames = {"room_id","actor_type","actor_id"}),
        indexes = {@Index(name="idx_member_lookup", columnList = "room_id,actor_type,actor_id")})
@Getter @Setter @NoArgsConstructor
public class ChatRoomMember {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "room_id", nullable = false, foreignKey = @ForeignKey(name="fk_member_room"))
    private ChatRoom room;

    @Column(name = "actor_type", nullable = false, length = 10)
    private String actorType; // 'user' | 'lawyer'

    @Column(name = "actor_id", nullable = false)
    private Long actorId;

    @Column(name = "last_seen_at")
    private Instant lastSeenAt;
}

