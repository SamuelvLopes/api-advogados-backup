package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Login.LoginRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Login.LoginResponseDTO;
import advogados_popular.api_advogados_popular.Entitys.Account;
import advogados_popular.api_advogados_popular.Repositorys.AccountRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AccountRepository accountRepository;

    public AuthService(AuthenticationManager authenticationManager, JwtService jwtService, AccountRepository accountRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.accountRepository = accountRepository;
    }

    public LoginResponseDTO autenticar(LoginRequestDTO dto) {
        var authenticationToken = new UsernamePasswordAuthenticationToken(dto.email(), dto.senha());
        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Email ou senha incorretos");
        }

        Account account = accountRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        String token = jwtService.gerarToken(account);
        String role = account.getRole().name();
        Long id;
        String nome;
        if (account.getUsuario() != null) {
            id = account.getUsuario().getId();
            nome = account.getUsuario().getNome();
        } else if (account.getAdvogado() != null) {
            id = account.getAdvogado().getId();
            nome = account.getAdvogado().getNome();
        } else {
            id = account.getId();
            nome = account.getEmail();
        }
        return new LoginResponseDTO(id, nome, account.getEmail(), role, token);

    }
}


