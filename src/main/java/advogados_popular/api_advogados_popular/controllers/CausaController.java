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

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<CausaResponseDTO> encerrar(@PathVariable("id") Long id,
                                                     @RequestParam("sucesso") boolean sucesso) {
        return ResponseEntity.ok(causaService.encerrar(id, sucesso));
    }
}



