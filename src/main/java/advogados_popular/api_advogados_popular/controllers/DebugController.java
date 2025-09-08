package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Entitys.Avaliacao;
import advogados_popular.api_advogados_popular.Entitys.Causa;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.Repositorys.AvaliacaoRepository;
import advogados_popular.api_advogados_popular.Repositorys.CausaRepository;
import advogados_popular.api_advogados_popular.Repositorys.PropostaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/debug")
public class DebugController {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PropostaRepository propostaRepository;
    private final AdvogadoRepository advogadoRepository;
    private final CausaRepository causaRepository;

    public DebugController(AvaliacaoRepository avaliacaoRepository,
                           PropostaRepository propostaRepository,
                           AdvogadoRepository advogadoRepository,
                           CausaRepository causaRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.propostaRepository = propostaRepository;
        this.advogadoRepository = advogadoRepository;
        this.causaRepository = causaRepository;
    }

    @GetMapping("/avaliacoes")
    public ResponseEntity<?> listarTodasAvaliacoes() {
        List<Map<String, Object>> out = avaliacaoRepository.findAll().stream()
                .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapAvaliacao)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    @GetMapping("/avaliacoes/por-advogado/{id}")
    public ResponseEntity<?> listarPorAdvogado(@PathVariable("id") Long id) {
        Advogado adv = advogadoRepository.findById(id).orElse(null);
        if (adv == null) {
            adv = advogadoRepository.findByAccount_Id(id).orElse(null);
        }
        if (adv == null) return ResponseEntity.ok(List.of());

        // Causas com proposta ACEITA do advogado
        List<Causa> causasAceitas = propostaRepository.findByAdvogado(adv).stream()
                .filter(p -> p.getStatus() == statusProposta.ACEITA)
                .map(p -> p.getCausa())
                .distinct()
                .collect(Collectors.toList());
        if (causasAceitas.isEmpty()) return ResponseEntity.ok(List.of());

        List<Map<String, Object>> out = avaliacaoRepository.findByCausaIn(causasAceitas).stream()
                .sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::mapAvaliacao)
                .collect(Collectors.toList());
        return ResponseEntity.ok(out);
    }

    private Map<String, Object> mapAvaliacao(Avaliacao a) {
        Long advogadoId = null;
        try { advogadoId = a.getCausa() != null && a.getCausa().getAdvogadoAtribuido() != null ? a.getCausa().getAdvogadoAtribuido().getId() : null; } catch (Exception ignore) {}
        return Map.of(
                "id", a.getId(),
                "causaId", a.getCausa() != null ? a.getCausa().getId() : null,
                "advogadoId", advogadoId,
                "usuarioId", a.getUsuario() != null ? a.getUsuario().getId() : null,
                "estrelas", a.getEstrelas(),
                "comentario", a.getComentario(),
                "createdAt", a.getCreatedAt()
        );
    }
}

