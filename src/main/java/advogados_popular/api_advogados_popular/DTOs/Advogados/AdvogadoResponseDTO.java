package advogados_popular.api_advogados_popular.DTOs.Advogados;

public record AdvogadoResponseDTO(Long id,
                                  String nome,
                                  String email,
                                  String oab,
                                  String whatsapp,
                                  String areasAtuacao) {}
