package advogados_popular.api_advogados_popular.Entitys;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.util.List;

// Chat.java
@Entity
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "lance_id")
    private Lance lance;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Mensagem> mensagens;

    private boolean propostaAceita;
}
