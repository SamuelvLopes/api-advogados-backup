package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.DTOs.Avaliacao.AvaliacaoResponseDTO;
import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoResponseDTO;
import advogados_popular.api_advogados_popular.sevices.AdvogadoService;
import advogados_popular.api_advogados_popular.sevices.AvaliacaoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/lawyers")
public class LawyerController {

    private final AdvogadoRepository advogadoRepository;
    private final AvaliacaoService avaliacaoService;
    private final AdvogadoService advogadoService;

    public LawyerController(AdvogadoRepository advogadoRepository, AvaliacaoService avaliacaoService,
                            AdvogadoService advogadoService) {
        this.advogadoRepository = advogadoRepository;
        this.avaliacaoService = avaliacaoService;
        this.advogadoService = advogadoService;
    }

    @GetMapping("/{id}/public-profile")
    public ResponseEntity<?> getPublicProfile(@PathVariable("id") Long id) {
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

    @PatchMapping("/me/profile")
    public ResponseEntity<AdvogadoResponseDTO> updateProfile(@RequestParam Long id,
                                                             @RequestBody AdvogadoRequestDTO dto) {
        return ResponseEntity.ok(advogadoService.atualizarPerfil(id, dto));
    }

    @GetMapping("/me/dashboard")
    public ResponseEntity<Map<String, Object>> dashboard(@RequestParam Long id) {
        return ResponseEntity.ok(advogadoService.dashboard(id));
    }
}
