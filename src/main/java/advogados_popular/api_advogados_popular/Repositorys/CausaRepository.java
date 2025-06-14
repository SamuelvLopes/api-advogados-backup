package advogados_popular.api_advogados_popular.Repositorys;

import advogados_popular.api_advogados_popular.Entitys.Causa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CausaRepository extends JpaRepository<Causa, Long> {
}
