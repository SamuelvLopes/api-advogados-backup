package advogados_popular.api_advogados_popular.DTOs.Causa;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Representa os dados retornados para uma {@link advogados_popular.api_advogados_popular.Entitys.Causa}.
 * Inclui a quantidade de propostas associadas à causa.
 */
@JsonInclude(Include.ALWAYS)
public record CausaResponseDTO(
        Long id,
        String titulo,
        String descricao,
        Long usuarioId,
        String usuarioNome,
        statusCausa status,
        long quantidadePropostas,
        Integer avaliacao, // opcional: estrelas 0-5 do usuário dono (se houver)
        String avaliacaoComentario // opcional: comentário do usuário
) {}

