package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.Avaliacao.AvaliacaoRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Avaliacao.AvaliacaoResponseDTO;
import advogados_popular.api_advogados_popular.sevices.AvaliacaoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    public AvaliacaoController(AvaliacaoService avaliacaoService) {
        this.avaliacaoService = avaliacaoService;
    }

    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> avaliar(@RequestBody AvaliacaoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(avaliacaoService.avaliar(dto));
    }

    @GetMapping("/advogado/{id}")
    public ResponseEntity<List<AvaliacaoResponseDTO>> listarPorAdvogado(@PathVariable("id") Long id) {
        return ResponseEntity.ok(avaliacaoService.listarPorAdvogado(id));
    }
}

