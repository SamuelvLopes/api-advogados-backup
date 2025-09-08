package advogados_popular.api_advogados_popular.controllers;

import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Entitys.Avaliacao;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import advogados_popular.api_advogados_popular.Repositorys.AvaliacaoRepository;
import advogados_popular.api_advogados_popular.Repositorys.PropostaRepository;
import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import advogados_popular.api_advogados_popular.sevices.MockReviewStore;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/perfis")
public class PerfilController {

    private final AdvogadoRepository advogadoRepository;
    private final MockReviewStore reviews; // legacy mock (fallback)
    private final AvaliacaoRepository avaliacaoRepository;
    private final PropostaRepository propostaRepository;

    public PerfilController(AdvogadoRepository advogadoRepository, MockReviewStore reviews, AvaliacaoRepository avaliacaoRepository, PropostaRepository propostaRepository) {
        this.advogadoRepository = advogadoRepository;
        this.reviews = reviews;
        this.avaliacaoRepository = avaliacaoRepository;
        this.propostaRepository = propostaRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable("id") Long id) {
        Advogado adv = advogadoRepository.findById(id).orElse(null);
        if (adv == null) {
            // Many clients link using account id; try mapping account->advogado
            adv = advogadoRepository.findByAccount_Id(id).orElse(null);
        }
        String nome = adv != null ? adv.getNome() : ("Profissional " + id);
        String oab = adv != null ? adv.getOab() : null;
        String email = adv != null && adv.getAccount() != null ? adv.getAccount().getEmail() : null;
        Long advogadoIdReal = adv != null ? adv.getId() : null;
        // Avaliações das causas atendidas por este advogado
        // 1) Tenta via vinculo direto na causa
        List<Avaliacao> reais = avaliacaoRepository.findByCausa_AdvogadoAtribuido_Id(id);
        // 2) Fallback via propostas ACEITAS (JPQL)
        if ((reais == null || reais.isEmpty()) && adv != null) {
            reais = avaliacaoRepository.findByAdvogadoViaPropostasAceitas(adv.getId());
        }
        // 3) Fallback definitivo: varre todas avaliacoes e confere por causa -> propostas ACEITAS do advogado
        if ((reais == null || reais.isEmpty()) && adv != null) {
            var todas = avaliacaoRepository.findAll();
            var coletadas = new java.util.ArrayList<Avaliacao>();
            for (var a : todas) {
                var causa = a.getCausa();
                boolean pertence = false;
                try {
                    if (causa != null && causa.getAdvogadoAtribuido() != null && causa.getAdvogadoAtribuido().getId().equals(adv.getId())) {
                        pertence = true;
                    }
                } catch (Exception ignore) {}
                if (!pertence && causa != null) {
                    try {
                        var props = propostaRepository.findByCausa(causa);
                        for (var p : props) {
                            if (p.getAdvogado() != null && p.getAdvogado().getId().equals(adv.getId()) && p.getStatus() == statusProposta.ACEITA) {
                                pertence = true; break;
                            }
                        }
                    } catch (Exception ignore) {}
                }
                if (pertence) coletadas.add(a);
            }
            reais = coletadas;
        }
        double rating = 0.0;
        List<Map<String, Object>> reviewDtos;
        List<Map<String, Object>> causasAvaliadasDtos;
        if (reais != null && !reais.isEmpty()) {
            rating = Math.round(reais.stream().mapToInt(a -> a.getEstrelas() == null ? 0 : a.getEstrelas()).average().orElse(0) * 10.0) / 10.0;
            var ordenadas = reais.stream().sorted((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt())).toList();
            reviewDtos = ordenadas.stream()
                    .map(a -> Map.<String, Object>of(
                            "id", a.getId(),
                            "author", a.getUsuario() != null ? a.getUsuario().getNome() : "Usuário",
                            "rating", a.getEstrelas() == null ? 0 : a.getEstrelas(),
                            "comment", a.getComentario(),
                            "createdAt", a.getCreatedAt() != null ? a.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : System.currentTimeMillis()
                    ))
                    .collect(Collectors.toList());
            causasAvaliadasDtos = ordenadas.stream()
                    .map(a -> Map.<String, Object>of(
                            "causaId", a.getCausa() != null ? a.getCausa().getId() : null,
                            "titulo", a.getCausa() != null ? a.getCausa().getTitulo() : null,
                            "status", a.getCausa() != null && a.getCausa().getStatus() != null ? a.getCausa().getStatus().name() : null,
                            "estrelas", a.getEstrelas(),
                            "comentario", a.getComentario(),
                            "createdAt", a.getCreatedAt() != null ? a.getCreatedAt().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() : System.currentTimeMillis()
                    ))
                    .collect(Collectors.toList());
        } else {
            // Sem avaliações reais
            reviewDtos = List.of();
            causasAvaliadasDtos = List.of();
        }

        // Count accepted proposals for this lawyer
        long propostasAceitas = 0L;
        if (adv != null) {
            propostasAceitas = propostaRepository.findByAdvogado(adv).stream()
                    .filter(p -> p.getStatus() == statusProposta.ACEITA)
                    .count();
        }

        long totalAvaliacoes = reviewDtos.size();

        return ResponseEntity.ok(Map.of(
                "id", advogadoIdReal != null ? advogadoIdReal : id,
                "name", nome,
                "oab", oab,
                "email", email,
                "rating", rating,
                "reviews", reviewDtos,
                "propostasAceitas", propostasAceitas,
                "totalAvaliacoes", totalAvaliacoes,
                "causasAvaliadas", causasAvaliadasDtos
        ));
    }
}
