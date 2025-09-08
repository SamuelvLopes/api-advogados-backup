package com.solidarios.case;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    @Autowired
    private LegalCaseRepository caseRepository;

    @PostMapping("/{caseId}/choose")
    @PreAuthorize("hasAuthority('CLIENT')")
    public LegalCase choose(@PathVariable Long caseId, @RequestParam PaymentType type, Principal principal) {
        LegalCase legalCase = caseRepository.findById(caseId).orElseThrow();
        if (!legalCase.getClient().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Not allowed");
        }
        legalCase.setPaymentType(type);
        return caseRepository.save(legalCase);
    }

    @PostMapping("/{caseId}/proof")
    @PreAuthorize("hasAuthority('CLIENT')")
    public LegalCase attachProof(@PathVariable Long caseId, @RequestParam String proof, Principal principal) {
        LegalCase legalCase = caseRepository.findById(caseId).orElseThrow();
        if (!legalCase.getClient().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Not allowed");
        }
        legalCase.setPaymentProof(proof);
        legalCase.setPaid(true);
        return caseRepository.save(legalCase);
    }
}
