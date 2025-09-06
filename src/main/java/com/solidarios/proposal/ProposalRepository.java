package com.solidarios.proposal;

import com.solidarios.case.LegalCase;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, Long> {
    List<Proposal> findByLegalCase(LegalCase legalCase);
}
