package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Causa.CausaRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Causa.CausaResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.statusCausa;
import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import advogados_popular.api_advogados_popular.Entitys.Account;
import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Entitys.Proposta;
import advogados_popular.api_advogados_popular.Entitys.Causa;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.Repositorys.PropostaRepository;
import advogados_popular.api_advogados_popular.Entitys.User;
import advogados_popular.api_advogados_popular.Repositorys.AccountRepository;
import advogados_popular.api_advogados_popular.Repositorys.CausaRepository;
import advogados_popular.api_advogados_popular.Repositorys.UserRepository;
import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class CausaService {

    private final AdvogadoRepository advogadoRepository;
    private final PropostaRepository propostaRepository;
    private final CausaRepository causaRepository;
    private final UserRepository usuarioRepository;
    private final AccountRepository accountRepository;

    public CausaService(CausaRepository causaRepository,
                        UserRepository usuarioRepository,
                        AccountRepository accountRepository,
                        AdvogadoRepository advogadoRepository,
                        PropostaRepository propostaRepository) {
        this.causaRepository = causaRepository;
        this.usuarioRepository = usuarioRepository;
        this.accountRepository = accountRepository;
        this.advogadoRepository = advogadoRepository;
        this.propostaRepository = propostaRepository;
    }

    public List<CausaResponseDTO> listarCausas() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (account.getRole() != Role.ADVOGADO) {
            throw new RuntimeException("Apenas advogados podem visualizar as causas.");
        }

        return causaRepository.findAll().stream()
                .map(causa -> new CausaResponseDTO(
                        causa.getId(),
                        causa.getTitulo(),
                        causa.getDescricao(),
                        causa.getUsuario().getId(),
                        causa.getUsuario().getNome(),
                        causa.getStatus(),
                        propostaRepository.countByCausa(causa)
                ))
                .toList();
    }

    public List<CausaResponseDTO> historico(String status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        final statusCausa filtro = status != null ? statusCausa.valueOf(status) : null;

        if (account.getRole() == Role.USUARIO) {
            User usuario = usuarioRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            return causaRepository.findByUsuario(usuario).stream()
                    .filter(c -> filtro == null || c.getStatus() == filtro)
                    .map(c -> new CausaResponseDTO(
                            c.getId(),
                            c.getTitulo(),
                            c.getDescricao(),
                            usuario.getId(),
                            usuario.getNome(),
                            c.getStatus(),
                            propostaRepository.countByCausa(c)
                    ))
                    .toList();
        } else if (account.getRole() == Role.ADVOGADO) {
            Advogado advogado = advogadoRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Advogado não encontrado"));
            return propostaRepository.findByAdvogado(advogado).stream()
                    .map(Proposta::getCausa)
                    .distinct()
                    .filter(c -> filtro == null || c.getStatus() == filtro)
                    .map(c -> new CausaResponseDTO(
                            c.getId(),
                            c.getTitulo(),
                            c.getDescricao(),
                            c.getUsuario().getId(),
                            c.getUsuario().getNome(),
                            c.getStatus(),
                            propostaRepository.countByCausa(c)
                    ))
                    .toList();
        } else {
            throw new RuntimeException("Perfil inválido");
        }
    }

    public CausaResponseDTO cadastrar(CausaRequestDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (account.getRole() != Role.USUARIO) {
            throw new RuntimeException("Apenas usuários podem cadastrar causas.");
        }

        User usuario = usuarioRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Causa causa = new Causa();
        causa.setTitulo(dto.titulo());
        causa.setDescricao(dto.descricao());
        causa.setUsuario(usuario);
        causa.setStatus(statusCausa.ABERTA);

        Causa salvo = causaRepository.save(causa);

        return new CausaResponseDTO(
                salvo.getId(),
                salvo.getTitulo(),
                salvo.getDescricao(),
                usuario.getId(),
                usuario.getNome(),
                salvo.getStatus(),
                0
        );
    }

    public void finalizarCausa(Long causaId) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        if (account.getRole() != Role.ADVOGADO) {
            throw new RuntimeException("Apenas advogados podem finalizar causas.");
        }

        Advogado advogado = advogadoRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Advogado não encontrado"));

        Causa causa = causaRepository.findById(causaId)
                .orElseThrow(() -> new RuntimeException("Causa não encontrada"));

        // Verifica se a proposta aceita é do advogado atual (ou se já está atribuído)
        boolean ehDoAdvogado = false;
        if (causa.getAdvogadoAtribuido() != null && causa.getAdvogadoAtribuido().getId().equals(advogado.getId())) {
            ehDoAdvogado = true;
        } else {
            ehDoAdvogado = propostaRepository.findByCausa(causa).stream()
                    .anyMatch(p -> p.getStatus() == statusProposta.ACEITA && p.getAdvogado().getId().equals(advogado.getId()));
        }
        if (!ehDoAdvogado) {
            throw new RuntimeException("Somente o advogado com proposta aceita pode finalizar.");
        }

        causa.setStatus(statusCausa.FECHADA);
        causaRepository.save(causa);
    }

    public void avaliarCausa(Long causaId, Integer estrelas) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
        if (account.getRole() != Role.USUARIO) {
            throw new RuntimeException("Apenas usuários podem avaliar causas.");
        }

        User usuario = usuarioRepository.findByAccount(account)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        Causa causa = causaRepository.findById(causaId)
                .orElseThrow(() -> new RuntimeException("Causa não encontrada"));
        if (!causa.getUsuario().getId().equals(usuario.getId())) {
            throw new RuntimeException("A causa não pertence ao usuário");
        }
        if (causa.getStatus() != statusCausa.FECHADA) {
            throw new RuntimeException("Só é possível avaliar após a finalização.");
        }
        int s = estrelas == null ? 0 : Math.max(0, Math.min(5, estrelas));
        // Por simplicidade, salvamos a nota no próprio objeto Causa por enquanto.
        // Em produção, crie uma entidade Avaliacao com UNIQUE(causa_id, usuario_id).
        // Se já existisse um campo rating, setar aqui. Como não existe, não persistimos além de LOG.
        System.out.println("[AVALIACAO] Causa " + causaId + " avaliada com " + s + " estrelas");
    }
}


