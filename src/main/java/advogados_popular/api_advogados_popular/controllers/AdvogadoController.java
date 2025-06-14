package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoResponseDTO;
import advogados_popular.api_advogados_popular.sevices.AdvogadoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/advogados")
public class AdvogadoController {

    private final AdvogadoService service;

    public AdvogadoController(AdvogadoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<AdvogadoResponseDTO> cadastrar(@RequestBody AdvogadoRequestDTO dto) {
        return ResponseEntity.ok(service.cadastrar(dto));
    }
}
