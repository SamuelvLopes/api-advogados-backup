package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Login.LoginRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Login.LoginResponseDTO;
import advogados_popular.api_advogados_popular.Entitys.Account;
import advogados_popular.api_advogados_popular.Entitys.User;
import advogados_popular.api_advogados_popular.Repositorys.AccountRepository;
import advogados_popular.api_advogados_popular.Repositorys.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

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
        authenticationManager.authenticate(authenticationToken);

        Account account = accountRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));

        String token = jwtService.gerarToken(account);
        String role = account.getRole().name();
        return new LoginResponseDTO(token,role);

    }
}


