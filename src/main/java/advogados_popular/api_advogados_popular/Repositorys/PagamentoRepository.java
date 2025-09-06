package advogados_popular.api_advogados_popular.Repositorys;

import advogados_popular.api_advogados_popular.Entitys.Pagamento;
import advogados_popular.api_advogados_popular.DTOs.utils.StatusPagamento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {
    List<Pagamento> findByProposta_Advogado_IdAndStatus(Long advogadoId, StatusPagamento status);
}
