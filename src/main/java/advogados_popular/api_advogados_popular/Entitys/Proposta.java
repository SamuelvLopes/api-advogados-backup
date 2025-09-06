package advogados_popular.api_advogados_popular.Entitys;

import advogados_popular.api_advogados_popular.DTOs.statusProposta;
import advogados_popular.api_advogados_popular.DTOs.utils.FormaPagamento;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "propostas", uniqueConstraints = @UniqueConstraint(columnNames = {"advogado_id", "causa_id"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Proposta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "advogado_id")
    private Advogado advogado;

    @ManyToOne
    @JoinColumn(name = "causa_id")
    private Causa causa;

    private String mensagem;

    @Column(name = "valor_sugerido")
    private BigDecimal valorSugerido;

    @Enumerated(EnumType.STRING)
    private statusProposta status;

    @Enumerated(EnumType.STRING)
    private FormaPagamento formaPagamento;

    @Column(name = "comprovante_pagamento")
    private String comprovantePagamento;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
