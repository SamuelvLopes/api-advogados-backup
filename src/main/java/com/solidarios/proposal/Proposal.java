package com.solidarios.proposal;

import com.solidarios.case.LegalCase;
import com.solidarios.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "proposals")
public class Proposal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LegalCase legalCase;

    @ManyToOne
    private User lawyer;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private ProposalStatus status = ProposalStatus.PENDING;
}
