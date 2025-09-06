package com.solidarios.evaluation;

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
@Table(name = "evaluations")
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private LegalCase legalCase;

    @ManyToOne
    private User client;

    @ManyToOne
    private User lawyer;

    private int rating;
    private String comment;
}
