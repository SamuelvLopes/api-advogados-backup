package advogados_popular.api_advogados_popular.Entitys;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.List;

// Causa.java
@Entity
public class Causa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titulo;
    private String descricao;
    private boolean atribuida;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private User usuario;

    @ManyToOne
    @JoinColumn(name = "advogado_id")
    private Advogado advogadoAtribuido;

    @OneToMany(mappedBy = "causa")
    private List<Lance> lances;
}
