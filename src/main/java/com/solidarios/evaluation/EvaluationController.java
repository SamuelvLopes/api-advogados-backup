package com.solidarios.evaluation;

import com.solidarios.case.CaseStatus;
import com.solidarios.case.LegalCase;
import com.solidarios.case.LegalCaseRepository;
import com.solidarios.user.User;
import com.solidarios.user.UserRepository;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/evaluations")
public class EvaluationController {

    @Autowired
    private EvaluationRepository evaluationRepository;
    @Autowired
    private LegalCaseRepository caseRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{caseId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public Evaluation evaluate(@PathVariable Long caseId, @RequestBody Evaluation evaluation, Principal principal) {
        LegalCase legalCase = caseRepository.findById(caseId).orElseThrow();
        if (!legalCase.getClient().getEmail().equals(principal.getName()) ||
                !(legalCase.getStatus() == CaseStatus.CLOSED_SUCCESS || legalCase.getStatus() == CaseStatus.CLOSED_FAILURE)) {
            throw new RuntimeException("Not allowed");
        }
        evaluation.setLegalCase(legalCase);
        evaluation.setClient(legalCase.getClient());
        evaluation.setLawyer(legalCase.getLawyer());
        return evaluationRepository.save(evaluation);
    }

    @GetMapping("/lawyer/{lawyerId}")
    public List<Evaluation> listByLawyer(@PathVariable Long lawyerId) {
        User lawyer = userRepository.findById(lawyerId).orElseThrow();
        return evaluationRepository.findByLawyer(lawyer);
    }
}
