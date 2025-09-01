package advogados_popular.api_advogados_popular.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/financeiro")
public class FinanceiroController {

    @GetMapping("/resumo")
    public ResponseEntity<Map<String, Object>> resumo() {
        List<String> months = Arrays.asList("Jan","Fev","Mar","Abr","Mai","Jun","Jul","Ago","Set","Out","Nov","Dez");
        List<Integer> values = new ArrayList<>();
        Random r = new Random(42);
        for (int i = 0; i < 12; i++) values.add(500 + r.nextInt(1500));
        return ResponseEntity.ok(Map.of("months", months, "values", values));
    }
}
