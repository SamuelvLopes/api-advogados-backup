package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.Causa.CausaRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Causa.CausaResponseDTO;
import advogados_popular.api_advogados_popular.sevices.CausaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/causas")
public class CausaController {

    private final CausaService causaService;

    public CausaController(CausaService causaService) {
        this.causaService = causaService;
    }

    @PostMapping
    public ResponseEntity<CausaResponseDTO> cadastrar(@RequestBody CausaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(causaService.cadastrar(dto));
    }

    @GetMapping
    public ResponseEntity<List<CausaResponseDTO>> listarCausas() {
        List<CausaResponseDTO> causas = causaService.listarCausas();
        return ResponseEntity.ok(causas);
    }

    @GetMapping("/historico")
    public ResponseEntity<List<CausaResponseDTO>> historico(@RequestParam(value = "status", required = false) String status) {
        return ResponseEntity.ok(causaService.historico(status));
    }

    @PostMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizar(@PathVariable("id") Long causaId) {
        causaService.finalizarCausa(causaId);
        return ResponseEntity.noContent().build();
    }

    public record AvaliacaoRequest(Integer estrelas) {}

    @PostMapping("/{id}/avaliacao")
    public ResponseEntity<Void> avaliar(@PathVariable("id") Long causaId, @RequestBody AvaliacaoRequest req) {
        causaService.avaliarCausa(causaId, req.estrelas());
        return ResponseEntity.noContent().build();
    }
}



