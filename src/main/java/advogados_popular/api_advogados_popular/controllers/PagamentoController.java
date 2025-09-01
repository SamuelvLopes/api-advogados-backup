package advogados_popular.api_advogados_popular.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    @PostMapping("/propostas/{id}")
    public ResponseEntity<Map<String, Object>> pagar(@PathVariable("id") Long id) {
        return ResponseEntity.ok(Map.of(
                "status", "paid",
                "transactionId", "TX-" + id + "-" + System.currentTimeMillis()
        ));
    }
}
