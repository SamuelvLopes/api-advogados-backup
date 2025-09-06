package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.DTOs.Avaliacao.AvaliacaoResponseDTO;
import advogados_popular.api_advogados_popular.sevices.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    private final AdvogadoRepository advogadoRepository;
    private final AvaliacaoService avaliacaoService;

    public PerfilController(AdvogadoRepository advogadoRepository, AvaliacaoService avaliacaoService) {
        this.advogadoRepository = advogadoRepository;
        this.avaliacaoService = avaliacaoService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        Advogado adv = advogadoRepository.findById(id).orElse(null);
        String nome = adv != null ? adv.getNome() : ("Profissional " + id);
        String oab = adv != null ? adv.getOab() : null;
        double rating = avaliacaoService.media(id);
        List<AvaliacaoResponseDTO> list = avaliacaoService.listarPorAdvogado(id);
        return ResponseEntity.ok(Map.of(
                "id", id,
                "name", nome,
                "oab", oab,
                "rating", rating,
                "reviews", list
        ));
    }
}
