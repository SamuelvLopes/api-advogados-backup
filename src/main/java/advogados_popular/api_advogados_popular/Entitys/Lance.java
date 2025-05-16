package advogados_popular.api_advogados_popular.Entitys;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.math.BigDecimal;

// Lance.java
@Entity
@Table(name = "lances")
public class Lance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal valor;

    @ManyToOne
    @JoinColumn(name = "causa_id")
    private Causa causa;

    @ManyToOne
    @JoinColumn(name = "advogado_id")
    private Advogado advogado;

    @OneToOne(mappedBy = "lance", cascade = CascadeType.ALL)
    private Chat chat;
}

