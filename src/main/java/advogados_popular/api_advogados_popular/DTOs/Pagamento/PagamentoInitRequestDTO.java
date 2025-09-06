package advogados_popular.api_advogados_popular.DTOs.Pagamento;

import advogados_popular.api_advogados_popular.DTOs.utils.FormaPagamento;
import advogados_popular.api_advogados_popular.DTOs.utils.MetodoPagamento;

public record PagamentoInitRequestDTO(Long propostaId, MetodoPagamento metodo, FormaPagamento quando) {}
