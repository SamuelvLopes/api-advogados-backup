package advogados_popular.api_advogados_popular.Repositorys;

import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import advogados_popular.api_advogados_popular.Entitys.Avaliacao;
import advogados_popular.api_advogados_popular.Entitys.Causa;
import advogados_popular.api_advogados_popular.Entitys.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Long> {
    Optional<Avaliacao> findByCausaAndUsuario(Causa causa, User usuario);

    List<Avaliacao> findByCausaIn(java.util.Collection<Causa> causas);

    @Query("""
            select a from Avaliacao a
            where (a.causa.advogadoAtribuido.id = :advogadoId)
               or exists (select 1 from Proposta p where p.causa = a.causa and p.advogado.id = :advogadoId and p.status = :statusAceita)
            order by a.createdAt desc
            """)
    List<Avaliacao> findAllLinkedToAdvogado(@Param("advogadoId") Long advogadoId,
                                            @Param("statusAceita") statusProposta statusAceita,
                                            Pageable pageable);

    @Query("""
            select count(a) from Avaliacao a
            where (a.causa.advogadoAtribuido.id = :advogadoId)
               or exists (select 1 from Proposta p where p.causa = a.causa and p.advogado.id = :advogadoId and p.status = :statusAceita)
            """)
    long countAllLinkedToAdvogado(@Param("advogadoId") Long advogadoId,
                                  @Param("statusAceita") statusProposta statusAceita);

    @Query("select a from Avaliacao a where a.causa.id = :causaId order by a.createdAt desc")
    List<Avaliacao> findByCausaIdOrderByCreatedAtDesc(@Param("causaId") Long causaId, Pageable pageable);
}
