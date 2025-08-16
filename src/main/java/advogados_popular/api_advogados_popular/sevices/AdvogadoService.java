package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import advogados_popular.api_advogados_popular.Entitys.Account;
import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Repositorys.AccountRepository;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AdvogadoService {
    private final AdvogadoRepository advogadoRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AdvogadoService(AdvogadoRepository advogadoRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.advogadoRepository = advogadoRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AdvogadoResponseDTO cadastrar(AdvogadoRequestDTO dto) {
        if (accountRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
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
        Advogado savedAdvogado = advogadoRepository.save(advogado);

        return new AdvogadoResponseDTO(savedAdvogado.getId(), savedAdvogado.getNome(), savedAdvogado.getOab(), savedAccount.getEmail());
    }
}

