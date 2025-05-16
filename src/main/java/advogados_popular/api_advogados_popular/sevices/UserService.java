package sevices;

import advogados_popular.api_advogados_popular.DTOs.User.UserRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.User.UserResponseDTO;
import advogados_popular.api_advogados_popular.Entitys.User;
import advogados_popular.api_advogados_popular.Repositorys.UserRepository;
import org.springframework.stereotype.Service;

// UserService.java
@Service
public class UserService {
    private final UserRepository repository;

    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public UserResponseDTO cadastrar(UserRequestDTO dto) {
        User user = new User();
        user.setNome(dto.nome());
        user.setEmail(dto.email());
        user.setSenha(dto.senha()); // criptografar em breve

        User salvo = repository.save(user);
        return new UserResponseDTO(salvo.getId(), salvo.getNome(), salvo.getEmail());
    }
}

