package advogados_popular.api_advogados_popular.DTOs.Proposta;

import advogados_popular.api_advogados_popular.DTOs.utils.FormaPagamento;

public record PropostaAceiteRequestDTO(FormaPagamento formaPagamento,
                                       String comprovantePagamento) {}
