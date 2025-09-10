package advogados_popular.api_advogados_popular.Repositorys;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;
import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import advogados_popular.api_advogados_popular.Entitys.Causa;
import advogados_popular.api_advogados_popular.Entitys.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CausaRepository extends JpaRepository<Causa, Long> {
    List<Causa> findByUsuario(User usuario);

    @Query("""
            select distinct c from Causa c
            left join c.avaliacoes a
            where (c.advogadoAtribuido.id = :advogadoId
                or exists (select 1 from Proposta p where p.causa = c and p.advogado.id = :advogadoId and p.status = :statusAceita))
              and ((:usarStatusFinalizado = true and c.status in :statusFinalizados) or (:usarStatusFinalizado = false and a.id is not null))
            order by coalesce(c.updatedAt, c.createdAt) desc
            """)
    List<Causa> findCausasFinalizadasDoAdvogado(@Param("advogadoId") Long advogadoId,
                                                @Param("statusAceita") statusProposta statusAceita,
                                                @Param("usarStatusFinalizado") boolean usarStatusFinalizado,
                                                @Param("statusFinalizados") List<statusCausa> statusFinalizados,
                                                Pageable pageable);

    @Query("""
            select count(distinct c) from Causa c
            left join c.avaliacoes a
            where (c.advogadoAtribuido.id = :advogadoId
                or exists (select 1 from Proposta p where p.causa = c and p.advogado.id = :advogadoId and p.status = :statusAceita))
              and ((:usarStatusFinalizado = true and c.status in :statusFinalizados) or (:usarStatusFinalizado = false and a.id is not null))
            """)
    long countCausasFinalizadasDoAdvogado(@Param("advogadoId") Long advogadoId,
                                          @Param("statusAceita") statusProposta statusAceita,
                                          @Param("usarStatusFinalizado") boolean usarStatusFinalizado,
                                          @Param("statusFinalizados") List<statusCausa> statusFinalizados);
}
