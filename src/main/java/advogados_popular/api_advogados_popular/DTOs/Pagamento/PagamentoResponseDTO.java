package advogados_popular.api_advogados_popular.DTOs.Pagamento;

import advogados_popular.api_advogados_popular.DTOs.utils.FormaPagamento;
import advogados_popular.api_advogados_popular.DTOs.utils.MetodoPagamento;
import advogados_popular.api_advogados_popular.DTOs.utils.StatusPagamento;
import java.math.BigDecimal;

public record PagamentoResponseDTO(Long id, Long propostaId, BigDecimal valor,
                                   MetodoPagamento metodo, FormaPagamento quando,
                                   StatusPagamento status, String comprovante) {}
