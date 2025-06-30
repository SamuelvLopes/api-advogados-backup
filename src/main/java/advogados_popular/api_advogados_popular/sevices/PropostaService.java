package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Proposta.PropostaRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Proposta.PropostaResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import advogados_popular.api_advogados_popular.Entitys.*;
import advogados_popular.api_advogados_popular.Repositorys.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PropostaService {

    private final PropostaRepository propostaRepository;
    private final AccountRepository accountRepository;
    private final AdvogadoRepository advogadoRepository;
    private final CausaRepository causaRepository;

    public PropostaService(PropostaRepository propostaRepository,
                           AccountRepository accountRepository,
                           AdvogadoRepository advogadoRepository,
                           CausaRepository causaRepository) {
        this.propostaRepository = propostaRepository;
        this.accountRepository = accountRepository;
        this.advogadoRepository = advogadoRepository;
        this.causaRepository = causaRepository;
    }

    public PropostaResponseDTO criar(PropostaRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        if (account.getRole() != Role.ADVOGADO) {
            throw new RuntimeException("Apenas advogados podem enviar propostas.");
        }

        Advogado advogado = advogadoRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Advogado não encontrado"));
        Causa causa = causaRepository.findById(dto.causaId())
                .orElseThrow(() -> new RuntimeException("Causa não encontrada"));

        if (propostaRepository.existsByAdvogadoAndCausa(advogado, causa)) {
            throw new RuntimeException("Proposta já enviada para esta causa");
        }

        Proposta proposta = new Proposta();
        proposta.setAdvogado(advogado);
        proposta.setCausa(causa);
        proposta.setMensagem(dto.mensagem());
        proposta.setValorSugerido(dto.valorSugerido());
        proposta.setStatus(statusProposta.ENVIADA);
        Proposta salva = propostaRepository.save(proposta);

        return new PropostaResponseDTO(salva.getId(), causa.getId(), advogado.getNome(),
                salva.getMensagem(), salva.getValorSugerido(), salva.getStatus());
    }

    public List<PropostaResponseDTO> listarPorCausa(Long causaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        Causa causa = causaRepository.findById(causaId)
                .orElseThrow(() -> new RuntimeException("Causa não encontrada"));

        if (account.getRole() != Role.USUARIO || !causa.getUsuario().getAccount().equals(account)) {
            throw new RuntimeException("Acesso negado");
        }

        return propostaRepository.findByCausa(causa).stream()
                .map(p -> new PropostaResponseDTO(p.getId(), causa.getId(), p.getAdvogado().getNome(),
                        p.getMensagem(), p.getValorSugerido(), p.getStatus()))
                .toList();
    }

    public List<Causa> causasComPropostasDoAdvogado(Advogado advogado) {
        return propostaRepository.findByAdvogado(advogado).stream()
                .map(Proposta::getCausa)
                .toList();
    }
}
