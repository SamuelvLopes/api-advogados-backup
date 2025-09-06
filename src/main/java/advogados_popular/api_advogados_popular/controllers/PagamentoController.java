package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.Pagamento.PagamentoInitRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Pagamento.PagamentoResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.Pagamento.UploadComprovanteRequestDTO;
import advogados_popular.api_advogados_popular.sevices.PagamentoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @PostMapping("/init")
    public ResponseEntity<PagamentoResponseDTO> init(@RequestBody PagamentoInitRequestDTO dto) {
        return ResponseEntity.ok(pagamentoService.init(dto));
    }

    @PostMapping("/{id}/upload-receipt")
    public ResponseEntity<PagamentoResponseDTO> upload(@PathVariable Long id,
                                                       @RequestBody UploadComprovanteRequestDTO dto) {
        return ResponseEntity.ok(pagamentoService.uploadReceipt(id, dto));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<PagamentoResponseDTO> pay(@PathVariable Long id) {
        return ResponseEntity.ok(pagamentoService.pay(id));
    }

    @GetMapping
    public ResponseEntity<List<PagamentoResponseDTO>> list() {
        return ResponseEntity.ok(pagamentoService.listAll());
    }
}
