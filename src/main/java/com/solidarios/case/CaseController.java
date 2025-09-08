package com.solidarios.case;

import com.solidarios.proposal.Proposal;
import com.solidarios.proposal.ProposalRepository;
import com.solidarios.proposal.ProposalStatus;
import com.solidarios.user.User;
import com.solidarios.user.UserRepository;
import com.solidarios.user.UserRole;
import java.security.Principal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cases")
public class CaseController {

    @Autowired
    private LegalCaseRepository caseRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProposalRepository proposalRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('CLIENT')")
    public LegalCase createCase(@RequestBody LegalCase legalCase, Principal principal) {
        User client = userRepository.findByEmail(principal.getName()).orElseThrow();
        legalCase.setClient(client);
        legalCase.setStatus(CaseStatus.OPEN);
        return caseRepository.save(legalCase);
    }

    @GetMapping("/open")
    @PreAuthorize("hasAuthority('LAWYER')")
    public List<LegalCase> listOpenCases() {
        return caseRepository.findByStatus(CaseStatus.OPEN);
    }

    @GetMapping("/history")
    public List<LegalCase> history(Principal principal) {
        User user = userRepository.findByEmail(principal.getName()).orElseThrow();
        if (user.getRole() == UserRole.CLIENT) {
            return caseRepository.findByClient(user);
        }
        return caseRepository.findByLawyer(user);
    }

    @PostMapping("/{id}/accept/{proposalId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public LegalCase acceptProposal(@PathVariable Long id, @PathVariable Long proposalId, Principal principal) {
        LegalCase legalCase = caseRepository.findById(id).orElseThrow();
        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow();
        if (!legalCase.getClient().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Not allowed");
        }
        proposal.setStatus(ProposalStatus.ACCEPTED);
        proposalRepository.save(proposal);
        legalCase.setLawyer(proposal.getLawyer());
        legalCase.setStatus(CaseStatus.IN_PROGRESS);
        legalCase.setAgreedAmount(proposal.getAmount());
        return caseRepository.save(legalCase);
    }

    @PostMapping("/{id}/close")
    @PreAuthorize("hasAuthority('LAWYER')")
    public LegalCase closeCase(@PathVariable Long id, @RequestParam boolean success, Principal principal) {
        LegalCase legalCase = caseRepository.findById(id).orElseThrow();
        if (!legalCase.getLawyer().getEmail().equals(principal.getName())) {
            throw new RuntimeException("Not allowed");
        }
        legalCase.setStatus(success ? CaseStatus.CLOSED_SUCCESS : CaseStatus.CLOSED_FAILURE);
        return caseRepository.save(legalCase);
    }
}
