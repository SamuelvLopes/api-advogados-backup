package com.solidarios.dashboard;

import com.solidarios.case.LegalCase;
import com.solidarios.case.LegalCaseRepository;
import com.solidarios.user.User;
import com.solidarios.user.UserRepository;
import java.security.Principal;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private LegalCaseRepository caseRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @PreAuthorize("hasAuthority('LAWYER')")
    public Metrics metrics(Principal principal) {
        User lawyer = userRepository.findByEmail(principal.getName()).orElseThrow();
        List<LegalCase> cases = caseRepository.findByLawyer(lawyer).stream()
                .filter(c -> Boolean.TRUE.equals(c.getPaid()))
                .toList();
        DoubleSummaryStatistics stats = cases.stream()
                .mapToDouble(c -> c.getAgreedAmount() != null ? c.getAgreedAmount() : 0)
                .summaryStatistics();
        long totalCases = cases.size();
        double totalReceived = stats.getSum();
        double averageTicket = stats.getAverage();
        double topCase = stats.getMax();
        return new Metrics(totalReceived, totalCases, averageTicket, topCase);
    }

    public record Metrics(double totalReceived, long numberOfCases, double averageTicket, double topCase) {}
}
