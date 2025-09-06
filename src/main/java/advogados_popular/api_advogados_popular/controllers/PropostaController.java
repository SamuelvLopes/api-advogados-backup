package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.Proposta.PropostaRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Proposta.PropostaResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.Proposta.PropostaAceiteRequestDTO;
import advogados_popular.api_advogados_popular.sevices.PropostaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/propostas")
public class PropostaController {

    private final PropostaService service;

    public PropostaController(PropostaService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<PropostaResponseDTO> criar(@RequestBody PropostaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(dto));
    }

    @GetMapping
    public ResponseEntity<List<PropostaResponseDTO>> listarPorCausa(@RequestParam("causa_id") Long causaId) {
        return ResponseEntity.ok(service.listarPorCausa(causaId));
    }

    @GetMapping("/minhas")
    public ResponseEntity<List<PropostaResponseDTO>> listarDoAdvogado() {
        return ResponseEntity.ok(service.listarDoAdvogado());
    }

    @PostMapping("/{id}/aceitar")
    public ResponseEntity<PropostaResponseDTO> aceitar(@PathVariable("id") Long id,
                                                       @RequestBody(required = false) PropostaAceiteRequestDTO dto) {
        return ResponseEntity.ok(service.aceitar(id, dto));
    }
}
