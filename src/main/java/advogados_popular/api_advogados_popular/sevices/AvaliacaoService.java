package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Avaliacao.AvaliacaoRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Avaliacao.AvaliacaoResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.statusCausa;
import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import advogados_popular.api_advogados_popular.Entitys.*;
import advogados_popular.api_advogados_popular.Repositorys.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final CausaRepository causaRepository;
    private final UserRepository userRepository;
    private final AdvogadoRepository advogadoRepository;
    private final AccountRepository accountRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository,
                            CausaRepository causaRepository,
                            UserRepository userRepository,
                            AdvogadoRepository advogadoRepository,
                            AccountRepository accountRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.causaRepository = causaRepository;
        this.userRepository = userRepository;
        this.advogadoRepository = advogadoRepository;
        this.accountRepository = accountRepository;
    }

    public AvaliacaoResponseDTO avaliar(AvaliacaoRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (account.getRole() != Role.USUARIO) {
            throw new RuntimeException("Apenas usuários podem avaliar");
        }

        User usuario = userRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        Causa causa = causaRepository.findById(dto.causaId())
                .orElseThrow(() -> new RuntimeException("Causa não encontrada"));

        if (!causa.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("Acesso negado");
        }

        if (causa.getStatus() != statusCausa.FECHADA || causa.getAdvogadoAtribuido() == null) {
            throw new RuntimeException("Causa ainda não encerrada");
        }

        if (avaliacaoRepository.existsByUsuarioAndCausa(usuario, causa)) {
            throw new RuntimeException("Avaliação já realizada");
        }

        Avaliacao avaliacao = new Avaliacao();
        avaliacao.setAdvogado(causa.getAdvogadoAtribuido());
        avaliacao.setUsuario(usuario);
        avaliacao.setCausa(causa);
        avaliacao.setNota(dto.nota());
        avaliacao.setComentario(dto.comentario());

        Avaliacao salva = avaliacaoRepository.save(avaliacao);

        return new AvaliacaoResponseDTO(
                salva.getId(),
                salva.getAdvogado().getId(),
                usuario.getNome(),
                salva.getNota(),
                salva.getComentario(),
                salva.getCreatedAt()
        );
    }

    public List<AvaliacaoResponseDTO> listarPorAdvogado(Long advogadoId) {
        Advogado advogado = advogadoRepository.findById(advogadoId)
                .orElseThrow(() -> new RuntimeException("Advogado não encontrado"));

        return avaliacaoRepository.findByAdvogado(advogado).stream()
                .map(a -> new AvaliacaoResponseDTO(
                        a.getId(),
                        advogado.getId(),
                        a.getUsuario().getNome(),
                        a.getNota(),
                        a.getComentario(),
                        a.getCreatedAt()
                ))
                .toList();
    }

    public double media(Long advogadoId) {
        List<AvaliacaoResponseDTO> list = listarPorAdvogado(advogadoId);
        return list.isEmpty() ? 0.0 :
                Math.round(list.stream().mapToInt(AvaliacaoResponseDTO::nota).average().orElse(0) * 10.0) / 10.0;
    }
}

