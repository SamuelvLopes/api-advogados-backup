package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.sevices.MockReviewStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    private final AdvogadoRepository advogadoRepository;
    private final MockReviewStore reviews;

    public PerfilController(AdvogadoRepository advogadoRepository, MockReviewStore reviews) {
        this.advogadoRepository = advogadoRepository;
        this.reviews = reviews;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        Advogado adv = advogadoRepository.findById(id).orElse(null);
        String nome = adv != null ? adv.getNome() : ("Profissional " + id);
        String oab = adv != null ? adv.getOab() : null;
        double rating = reviews.average(id);
        List<MockReviewStore.Review> list = reviews.list(id);
        return ResponseEntity.ok(Map.of(
                "id", id,
                "name", nome,
                "oab", oab,
                "rating", rating,
                "reviews", list
        ));
    }

    @PostMapping("/{id}/reviews")
    public ResponseEntity<?> add(@PathVariable("id") Long id,
                                 @RequestBody Map<String, Object> body) {
        String author = String.valueOf(body.getOrDefault("author", "An�nimo"));
        int rating = Integer.parseInt(String.valueOf(body.getOrDefault("rating", 5)));
        String comment = String.valueOf(body.getOrDefault("comment", ""));
        var r = reviews.add(id, author, rating, comment);
        return ResponseEntity.ok(r);
    }
}
