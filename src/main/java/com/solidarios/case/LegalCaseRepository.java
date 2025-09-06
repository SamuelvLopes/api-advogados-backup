package com.solidarios.case;

import com.solidarios.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LegalCaseRepository extends JpaRepository<LegalCase, Long> {
    List<LegalCase> findByStatus(CaseStatus status);
    List<LegalCase> findByClient(User client);
    List<LegalCase> findByLawyer(User lawyer);
}
