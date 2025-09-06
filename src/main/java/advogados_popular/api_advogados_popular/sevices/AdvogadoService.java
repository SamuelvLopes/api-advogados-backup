package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import advogados_popular.api_advogados_popular.DTOs.utils.StatusPagamento;
import advogados_popular.api_advogados_popular.Entitys.Account;
import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Entitys.Pagamento;
import advogados_popular.api_advogados_popular.Repositorys.AccountRepository;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.Repositorys.CausaRepository;
import advogados_popular.api_advogados_popular.Repositorys.PagamentoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Service
public class AdvogadoService {
    private final AdvogadoRepository advogadoRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final PagamentoRepository pagamentoRepository;
    private final CausaRepository causaRepository;

    public AdvogadoService(AdvogadoRepository advogadoRepository, AccountRepository accountRepository,
                           PasswordEncoder passwordEncoder, PagamentoRepository pagamentoRepository,
                           CausaRepository causaRepository) {
        this.advogadoRepository = advogadoRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
        this.pagamentoRepository = pagamentoRepository;
        this.causaRepository = causaRepository;
    }

    public AdvogadoResponseDTO cadastrar(AdvogadoRequestDTO dto) {
        if (accountRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        if (advogadoRepository.findByOab(dto.oab()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "OAB já cadastrada");
        }

        Account account = new Account();
        account.setEmail(dto.email());
        account.setSenha(passwordEncoder.encode(dto.senha()));
        account.setRole(Role.ADVOGADO);
        Account savedAccount = accountRepository.save(account);

        Advogado advogado = new Advogado();
        advogado.setAccount(savedAccount);
        advogado.setNome(dto.nome());
        advogado.setOab(dto.oab());
        advogado.setWhatsapp(dto.whatsapp());
        advogado.setAreasAtuacao(dto.areasAtuacao());
        Advogado savedAdvogado = advogadoRepository.save(advogado);

        return new AdvogadoResponseDTO(
                savedAdvogado.getId(),
                savedAdvogado.getNome(),
                savedAccount.getEmail(),
                savedAdvogado.getOab(),
                savedAdvogado.getWhatsapp(),
                savedAdvogado.getAreasAtuacao()
        );
    }

    public AdvogadoResponseDTO atualizarPerfil(Long id, AdvogadoRequestDTO dto) {
        Advogado adv = advogadoRepository.findById(id).orElseThrow();
        adv.setWhatsapp(dto.whatsapp());
        adv.setAreasAtuacao(dto.areasAtuacao());
        Advogado saved = advogadoRepository.save(adv);
        return new AdvogadoResponseDTO(
                saved.getId(),
                saved.getNome(),
                saved.getAccount().getEmail(),
                saved.getOab(),
                saved.getWhatsapp(),
                saved.getAreasAtuacao()
        );
    }

    public Map<String, Object> dashboard(Long id) {
        List<Pagamento> pagos = pagamentoRepository.findByProposta_Advogado_IdAndStatus(id, StatusPagamento.PAGO);
        BigDecimal total = pagos.stream()
                .map(Pagamento::getValor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long casos = causaRepository.countByAdvogadoAtribuido_Id(id);
        BigDecimal ticketMedio = casos > 0 ? total.divide(BigDecimal.valueOf(casos)) : BigDecimal.ZERO;
        Pagamento top = pagos.stream().max(Comparator.comparing(Pagamento::getValor)).orElse(null);
        Long topCaso = top != null ? top.getProposta().getCausa().getId() : null;
        return Map.of(
                "totalRecebido", total,
                "numeroDeCasos", casos,
                "ticketMedio", ticketMedio,
                "topCaso", topCaso
        );
    }
}
