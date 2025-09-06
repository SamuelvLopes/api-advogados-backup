package advogados_popular.api_advogados_popular.Entitys;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "avaliacoes", uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "causa_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Avaliacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "advogado_id")
    private Advogado advogado;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "causa_id")
    private Causa causa;

    private int nota;

    @Column(columnDefinition = "TEXT")
    private String comentario;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

