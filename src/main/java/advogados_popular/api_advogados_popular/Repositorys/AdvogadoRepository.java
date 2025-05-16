package advogados_popular.api_advogados_popular.Repositorys;

import advogados_popular.api_advogados_popular.Entitys.Advogado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdvogadoRepository extends JpaRepository<Advogado, Long> {
    Optional<Advogado> findByEmail(String email);
}