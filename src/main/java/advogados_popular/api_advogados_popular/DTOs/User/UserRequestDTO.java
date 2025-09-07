package advogados_popular.api_advogados_popular.DTOs.User;

import advogados_popular.api_advogados_popular.DTOs.utils.Role;

public record UserRequestDTO(String nome, String email, String senha, Role role, String telefone) {}
