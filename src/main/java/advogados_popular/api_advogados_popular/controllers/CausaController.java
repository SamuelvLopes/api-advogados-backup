package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.Causa.CausaRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Causa.CausaResponseDTO;
import advogados_popular.api_advogados_popular.sevices.CausaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/causas")
public class CausaController {

    private final CausaService causaService;

    public CausaController(CausaService causaService) {
        this.causaService = causaService;
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody CausaRequestDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(causaService.cadastrar(dto));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        }
    }

    @GetMapping
    public ResponseEntity<?> listarCausas() {
        try {
            List<CausaResponseDTO> causas = causaService.listarCausas();
            return ResponseEntity.ok(causas);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        }
    }

    @GetMapping("/historico")
    public ResponseEntity<?> historico(@RequestParam(value = "status", required = false) String status) {
        try {
            return ResponseEntity.ok(causaService.historico(status));
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("message", e.getReason()));
        }
    }
}



