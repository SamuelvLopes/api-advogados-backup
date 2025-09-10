package advogados_popular.api_advogados_popular.Entitys;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

import advogados_popular.api_advogados_popular.Entitys.Avaliacao;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "causas")
public class Causa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    @Column(columnDefinition = "LONGTEXT")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;


    @ManyToOne
    @JoinColumn(name = "advogado_id")
    private Advogado advogadoAtribuido;

    private statusCausa status;

    @OneToMany(mappedBy = "causa")
    private List<Lance> lances;

    @OneToMany(mappedBy = "causa")
    private List<Avaliacao> avaliacoes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
