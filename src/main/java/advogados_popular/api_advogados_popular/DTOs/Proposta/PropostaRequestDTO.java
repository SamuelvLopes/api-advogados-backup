package advogados_popular.api_advogados_popular.DTOs.Proposta;

import java.math.BigDecimal;

public record PropostaRequestDTO(Long causaId, String mensagem, BigDecimal valorSugerido) {}
