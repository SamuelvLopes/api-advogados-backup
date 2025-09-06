package com.solidarios.evaluation;

import com.solidarios.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByLawyer(User lawyer);
}
