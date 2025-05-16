package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.User.UserRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.User.UserResponseDTO;
import advogados_popular.api_advogados_popular.sevices.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// UserController.java
@RestController
@RequestMapping("/usuarios")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponseDTO> cadastrar(@RequestBody UserRequestDTO dto) {
        return ResponseEntity.ok(service.cadastrar(dto));
    }
}

