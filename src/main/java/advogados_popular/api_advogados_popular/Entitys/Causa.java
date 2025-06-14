package advogados_popular.api_advogados_popular.Entitys;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;
import jakarta.persistence.*;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

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
}
