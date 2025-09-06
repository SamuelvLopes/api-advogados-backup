package advogados_popular.api_advogados_popular.DTOs.Advogados;

import advogados_popular.api_advogados_popular.DTOs.utils.Role;

public record AdvogadoRequestDTO(String nome,
                                 String email,
                                 String senha,
                                 String oab,
                                 String whatsapp,
                                 String areasAtuacao,
                                 Role role) {}

