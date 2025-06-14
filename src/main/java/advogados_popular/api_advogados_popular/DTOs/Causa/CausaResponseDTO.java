package advogados_popular.api_advogados_popular.DTOs.Causa;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;

public record CausaResponseDTO(Long id, String titulo, String descricao, String usuarioNome, statusCausa status) {}

