package advogados_popular.api_advogados_popular.Entitys;

import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "advogados")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Advogado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "account_id", unique = true, nullable = false)
    private Account account;

    @Column(nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String oab;

    @Column(nullable = false)
    private String whatsapp;

    @Column(name = "areas_atuacao")
    private String areasAtuacao;

    @OneToMany(mappedBy = "advogado")
    private List<Lance> lances;
}

