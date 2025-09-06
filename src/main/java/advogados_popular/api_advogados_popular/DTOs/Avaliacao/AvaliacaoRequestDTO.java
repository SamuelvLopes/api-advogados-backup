package advogados_popular.api_advogados_popular.DTOs.Avaliacao;

public record AvaliacaoRequestDTO(
        Long causaId,
        int nota,
        String comentario
) {}

