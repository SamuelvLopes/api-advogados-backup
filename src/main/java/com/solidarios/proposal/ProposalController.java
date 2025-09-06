package com.solidarios.proposal;

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
@RequestMapping("/proposals")
public class ProposalController {

    @Autowired
    private ProposalRepository proposalRepository;
    @Autowired
    private LegalCaseRepository caseRepository;
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/{caseId}")
    @PreAuthorize("hasAuthority('LAWYER')")
    public Proposal sendProposal(@PathVariable Long caseId, @RequestBody Proposal proposal, Principal principal) {
        LegalCase legalCase = caseRepository.findById(caseId).orElseThrow();
        User lawyer = userRepository.findByEmail(principal.getName()).orElseThrow();
        proposal.setLegalCase(legalCase);
        proposal.setLawyer(lawyer);
        proposal.setStatus(ProposalStatus.PENDING);
        return proposalRepository.save(proposal);
    }

    @GetMapping("/{caseId}")
    public List<Proposal> list(@PathVariable Long caseId) {
        LegalCase legalCase = caseRepository.findById(caseId).orElseThrow();
        return proposalRepository.findByLegalCase(legalCase);
    }
}
