package advogados_popular.api_advogados_popular.Entitys;

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
    private String nome;
    private String email;
    private String senha;
    private String oab;

    @OneToMany(mappedBy = "advogado")
    private List<Lance> lances;
}

