package advogados_popular.api_advogados_popular.Repositorys;

import advogados_popular.api_advogados_popular.Entitys.Causa;
import advogados_popular.api_advogados_popular.Entitys.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CausaRepository extends JpaRepository<Causa, Long> {
    List<Causa> findByUsuario(User usuario);
    long countByAdvogadoAtribuido_Id(Long advogadoId);
}
