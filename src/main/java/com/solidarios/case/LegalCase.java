package com.solidarios.case;

import com.solidarios.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cases")
public class LegalCase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;

    @ManyToOne
    private User client;

    @ManyToOne
    private User lawyer;

    @Enumerated(EnumType.STRING)
    private CaseStatus status = CaseStatus.OPEN;

    private Double agreedAmount;

    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;

    private String paymentProof;

    private Boolean paid = false;
}
