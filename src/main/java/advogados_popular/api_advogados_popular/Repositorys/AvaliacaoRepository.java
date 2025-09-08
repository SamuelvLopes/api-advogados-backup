package advogados_popular.api_advogados_popular.Repositorys;

import advogados_popular.api_advogados_popular.Entitys.Avaliacao;
import advogados_popular.api_advogados_popular.Entitys.Causa;
import advogados_popular.api_advogados_popular.Entitys.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByCausaAndUsuario(Causa causa, User usuario);
    List<Avaliacao> findByCausa_AdvogadoAtribuido_Id(Long advogadoId);
    List<Avaliacao> findByCausaIn(java.util.Collection<Causa> causas);

    // Busca avaliações cujas causas pertencem a propostas ACEITAS do advogado
    @Query("select a from Avaliacao a where a.causa in (select p.causa from Proposta p where p.advogado.id = :advId and p.status = advogados_popular.api_advogados_popular.DTOs.statusProposta.ACEITA)")
    List<Avaliacao> findByAdvogadoViaPropostasAceitas(@Param("advId") Long advogadoId);
}
