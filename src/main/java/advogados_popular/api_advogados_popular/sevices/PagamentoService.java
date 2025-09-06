package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Pagamento.PagamentoInitRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Pagamento.PagamentoResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.Pagamento.UploadComprovanteRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.utils.StatusPagamento;
import advogados_popular.api_advogados_popular.Entitys.Pagamento;
import advogados_popular.api_advogados_popular.Entitys.Proposta;
import advogados_popular.api_advogados_popular.Repositorys.PagamentoRepository;
import advogados_popular.api_advogados_popular.Repositorys.PropostaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PagamentoService {
    private final PagamentoRepository pagamentoRepository;
    private final PropostaRepository propostaRepository;

    public PagamentoService(PagamentoRepository pagamentoRepository, PropostaRepository propostaRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.propostaRepository = propostaRepository;
    }

    @Transactional
    public PagamentoResponseDTO init(PagamentoInitRequestDTO dto) {
        Proposta proposta = propostaRepository.findById(dto.propostaId()).orElseThrow();
        Pagamento pagamento = new Pagamento();
        pagamento.setProposta(proposta);
        pagamento.setValor(proposta.getValorSugerido());
        pagamento.setMetodo(dto.metodo());
        pagamento.setQuando(dto.quando());
        pagamento.setStatus(StatusPagamento.PENDENTE);
        Pagamento saved = pagamentoRepository.save(pagamento);
        return toDto(saved);
    }

    @Transactional
    public PagamentoResponseDTO uploadReceipt(Long id, UploadComprovanteRequestDTO dto) {
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow();
        pagamento.setComprovante(dto.comprovante());
        pagamento.setStatus(StatusPagamento.PAGO);
        return toDto(pagamento);
    }

    @Transactional
    public PagamentoResponseDTO pay(Long id) {
        Pagamento pagamento = pagamentoRepository.findById(id).orElseThrow();
        pagamento.setStatus(StatusPagamento.PAGO);
        return toDto(pagamento);
    }

    public List<PagamentoResponseDTO> listAll() {
        return pagamentoRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    private PagamentoResponseDTO toDto(Pagamento p) {
        return new PagamentoResponseDTO(
                p.getId(),
                p.getProposta().getId(),
                p.getValor(),
                p.getMetodo(),
                p.getQuando(),
                p.getStatus(),
                p.getComprovante()
        );
    }
}
