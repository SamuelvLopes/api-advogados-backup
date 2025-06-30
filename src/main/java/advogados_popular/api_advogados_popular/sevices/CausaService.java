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
                        causa.getUsuario().getNome(),
                        causa.getStatus()
                ))
                .toList();
    }

    public List<CausaResponseDTO> historico(String status) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        statusCausa filtro = null;
        if (status != null) {
            filtro = statusCausa.valueOf(status);
        }

        if (account.getRole() == Role.USUARIO) {
            User usuario = usuarioRepository.findByAccount(account)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
            return causaRepository.findByUsuario(usuario).stream()
                    .filter(c -> filtro == null || c.getStatus() == filtro)
                    .map(c -> new CausaResponseDTO(
                            c.getId(),
                            c.getTitulo(),
                            c.getDescricao(),
                            usuario.getNome(),
                            c.getStatus()
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
                            c.getUsuario().getNome(),
                            c.getStatus()
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
                usuario.getNome(),
                salvo.getStatus()
        );
    }
}


