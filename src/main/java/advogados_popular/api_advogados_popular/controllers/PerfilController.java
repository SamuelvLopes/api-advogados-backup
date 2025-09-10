package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.DTOs.statusCausa;
import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Entitys.Avaliacao;
import advogados_popular.api_advogados_popular.Entitys.Causa;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.Repositorys.AvaliacaoRepository;
import advogados_popular.api_advogados_popular.Repositorys.CausaRepository;
import advogados_popular.api_advogados_popular.Repositorys.PropostaRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    private final AdvogadoRepository advogadoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final PropostaRepository propostaRepository;
    private final CausaRepository causaRepository;

    public PerfilController(AdvogadoRepository advogadoRepository,
                            AvaliacaoRepository avaliacaoRepository,
                            PropostaRepository propostaRepository,
                            CausaRepository causaRepository) {
        this.advogadoRepository = advogadoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.propostaRepository = propostaRepository;
        this.causaRepository = causaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id,
                                 @RequestParam(value = "limit", required = false, defaultValue = "20") Integer limit,
                                 @RequestParam(value = "limitCausas", required = false, defaultValue = "20") Integer limitCausas,
                                 @RequestParam(value = "limitReviewsPorCausa", required = false, defaultValue = "10") Integer limitReviewsPorCausa) {
        int lim = clamp(limit, 1, 100);
        int limCausas = clamp(limitCausas, 1, 100);
        int limReviewsCausa = clamp(limitReviewsPorCausa, 1, 100);

        Advogado adv = advogadoRepository.findById(id).orElse(null);
        if (adv == null) {
            adv = advogadoRepository.findByAccount_Id(id).orElse(null);
        }
        Long advogadoIdReal = adv != null ? adv.getId() : null;
        String nome = adv != null ? adv.getNome() : ("Profissional " + id);
        String oab = adv != null ? adv.getOab() : null;
        String email = adv != null && adv.getAccount() != null ? adv.getAccount().getEmail() : null;

        ZoneId zone = ZoneId.of("America/Recife");

        List<Avaliacao> reviews = advogadoIdReal != null
                ? avaliacaoRepository.findAllLinkedToAdvogado(advogadoIdReal, statusProposta.ACEITA, PageRequest.of(0, lim))
                : List.of();
        long totalAvaliacoes = advogadoIdReal != null
                ? avaliacaoRepository.countAllLinkedToAdvogado(advogadoIdReal, statusProposta.ACEITA)
                : 0L;
        double rating = 0.0;
        if (!reviews.isEmpty()) {
            rating = Math.round(reviews.stream()
                    .mapToInt(a -> a.getEstrelas() == null ? 0 : a.getEstrelas())
                    .average().orElse(0.0) * 10.0) / 10.0;
        }
        List<Map<String, Object>> reviewDtos = reviews.stream().map(a -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", a.getId());
            m.put("author", a.getUsuario() != null ? a.getUsuario().getNome() : "Usuário");
            m.put("rating", a.getEstrelas() == null ? 0 : a.getEstrelas());
            m.put("comment", a.getComentario());
            m.put("createdAt", a.getCreatedAt() != null ? a.getCreatedAt().atZone(zone).toInstant().toEpochMilli() : null);
            return m;
        }).collect(Collectors.toList());

        List<statusCausa> statusFinalizados = List.of(statusCausa.FECHADA);
        boolean usarStatus = !statusFinalizados.isEmpty();
        List<Causa> causas = advogadoIdReal != null
                ? causaRepository.findCausasFinalizadasDoAdvogado(advogadoIdReal, statusProposta.ACEITA, usarStatus, statusFinalizados, PageRequest.of(0, limCausas))
                : List.of();
        long totalCausasFinalizadas = advogadoIdReal != null
                ? causaRepository.countCausasFinalizadasDoAdvogado(advogadoIdReal, statusProposta.ACEITA, usarStatus, statusFinalizados)
                : 0L;
        List<Map<String, Object>> causasDtos = new ArrayList<>();
        for (Causa c : causas) {
            List<Avaliacao> revs = avaliacaoRepository.findByCausaIdOrderByCreatedAtDesc(c.getId(), PageRequest.of(0, limReviewsCausa));
            List<Map<String, Object>> revDtos = revs.stream().map(r -> {
                Map<String, Object> rm = new LinkedHashMap<>();
                rm.put("id", r.getId());
                rm.put("author", r.getUsuario() != null ? r.getUsuario().getNome() : "Usuário");
                rm.put("rating", r.getEstrelas() == null ? 0 : r.getEstrelas());
                rm.put("comment", r.getComentario());
                rm.put("createdAt", r.getCreatedAt() != null ? r.getCreatedAt().atZone(zone).toInstant().toEpochMilli() : null);
                return rm;
            }).collect(Collectors.toList());
            Map<String, Object> cm = new LinkedHashMap<>();
            cm.put("causaId", c.getId());
            cm.put("titulo", c.getTitulo());
            cm.put("status", c.getStatus() != null ? c.getStatus().name() : null);
            cm.put("createdAt", c.getCreatedAt() != null ? c.getCreatedAt().atZone(zone).toInstant().toEpochMilli() : null);
            cm.put("updatedAt", c.getUpdatedAt() != null ? c.getUpdatedAt().atZone(zone).toInstant().toEpochMilli() : null);
            cm.put("reviews", revDtos);
            causasDtos.add(cm);
        }

        long propostasAceitas = adv != null ? propostaRepository.countByAdvogadoAndStatus(adv, statusProposta.ACEITA) : 0L;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", advogadoIdReal != null ? advogadoIdReal : id);
        payload.put("name", nome);
        payload.put("oab", oab);
        payload.put("email", email);
        payload.put("rating", rating);
        payload.put("reviews", reviewDtos);
        payload.put("totalAvaliacoes", totalAvaliacoes);
        payload.put("propostasAceitas", propostasAceitas);
        payload.put("causasFinalizadas", causasDtos);
        payload.put("totalCausasFinalizadas", totalCausasFinalizadas);

        return ResponseEntity.ok(payload);
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
