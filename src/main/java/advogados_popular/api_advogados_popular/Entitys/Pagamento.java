package advogados_popular.api_advogados_popular.Entitys;

import advogados_popular.api_advogados_popular.DTOs.utils.FormaPagamento;
import advogados_popular.api_advogados_popular.DTOs.utils.MetodoPagamento;
import advogados_popular.api_advogados_popular.DTOs.utils.StatusPagamento;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "pagamentos")
@Getter
@Setter
public class Pagamento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "proposta_id")
    private Proposta proposta;

    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    private MetodoPagamento metodo;

    @Enumerated(EnumType.STRING)
    private FormaPagamento quando;

    @Enumerated(EnumType.STRING)
    private StatusPagamento status;

    private String comprovante;
}
