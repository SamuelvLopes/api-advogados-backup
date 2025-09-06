package advogados_popular.api_advogados_popular.DTOs.Causa;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;

/**
 * Representa os dados retornados para uma {@link advogados_popular.api_advogados_popular.Entitys.Causa}.
 * Inclui a quantidade de propostas associadas à causa.
 */
public record CausaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        Long usuarioId,
        String usuarioNome,
        statusCausa status,
        Boolean sucesso,
        long quantidadePropostas
) {}

