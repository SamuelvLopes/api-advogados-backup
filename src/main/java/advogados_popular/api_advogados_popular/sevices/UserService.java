package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.User.UserRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.User.UserResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import advogados_popular.api_advogados_popular.Entitys.Account;
import advogados_popular.api_advogados_popular.Entitys.User;
import advogados_popular.api_advogados_popular.Repositorys.AccountRepository;
import advogados_popular.api_advogados_popular.Repositorys.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService { private final UserRepository usuarioRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository usuarioRepository, AccountRepository accountRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDTO cadastrar(UserRequestDTO dto) {
        if (accountRepository.findByEmail(dto.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já cadastrado");
        }

        Account account = new Account();
        account.setEmail(dto.email());
        account.setSenha(passwordEncoder.encode(dto.senha()));
        account.setRole(Role.USUARIO);
        Account savedAccount = accountRepository.save(account);

        User usuario = new User();
        usuario.setAccount(savedAccount);
        usuario.setNome(dto.nome());
        usuario.setTelefone(dto.telefone());
        User savedUsuario = usuarioRepository.save(usuario);

        return new UserResponseDTO(savedUsuario.getId(), savedUsuario.getNome(), savedAccount.getEmail(), savedUsuario.getTelefone());
    }
}

