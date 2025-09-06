package advogados_popular.api_advogados_popular.DTOs.Avaliacao;

import java.time.LocalDateTime;

public record AvaliacaoResponseDTO(
        Long id,
        Long advogadoId,
        String autor,
        int nota,
        String comentario,
        LocalDateTime createdAt
) {}

