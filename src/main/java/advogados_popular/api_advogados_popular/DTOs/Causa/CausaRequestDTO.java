package advogados_popular.api_advogados_popular.DTOs.Causa;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;

public record CausaRequestDTO(
        String titulo,
        String descricao,
        statusCausa status
) {}
