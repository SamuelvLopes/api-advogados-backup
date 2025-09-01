package advogados_popular.api_advogados_popular.DTOs.Proposta;

import advogados_popular.api_advogados_popular.DTOs.statusProposta;

import java.math.BigDecimal;

public record PropostaResponseDTO(Long id,
                                  Long causaId,
                                  Long advogadoId,
                                  String advogadoNome,
                                  Long usuarioId,
                                  String mensagem,
                                  BigDecimal valorSugerido,
                                  statusProposta status) {}

