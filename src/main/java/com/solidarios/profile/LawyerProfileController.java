package com.solidarios.profile;

import com.solidarios.evaluation.Evaluation;
import com.solidarios.evaluation.EvaluationRepository;
import com.solidarios.user.User;
import com.solidarios.user.UserRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/lawyers")
public class LawyerProfileController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EvaluationRepository evaluationRepository;

    @GetMapping("/{id}")
    public LawyerProfile profile(@PathVariable Long id) {
        User lawyer = userRepository.findById(id).orElseThrow();
        List<Evaluation> evaluations = evaluationRepository.findByLawyer(lawyer);
        double avg = evaluations.stream().mapToInt(Evaluation::getRating).average().orElse(0);
        return new LawyerProfile(lawyer.getName(), avg, evaluations);
    }

    public record LawyerProfile(String name, double averageRating, List<Evaluation> evaluations) {}
}
