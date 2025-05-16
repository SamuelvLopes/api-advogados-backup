package advogados_popular.api_advogados_popular.Entitys;

import jakarta.persistence.*;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

// Mensagem.java
@Entity
@Table(name = "mensagens")
public class Mensagem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String conteudo;
    private LocalDateTime enviadaEm;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private String remetente; // "USUARIO" ou "ADVOGADO"
}
