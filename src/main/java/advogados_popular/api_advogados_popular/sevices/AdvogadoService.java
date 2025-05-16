package advogados_popular.api_advogados_popular.sevices;

import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoRequestDTO;
import advogados_popular.api_advogados_popular.DTOs.Advogados.AdvogadoResponseDTO;
import advogados_popular.api_advogados_popular.Entitys.Advogado;
import advogados_popular.api_advogados_popular.Repositorys.AdvogadoRepository;
import org.springframework.stereotype.Service;

@Service
public class AdvogadoService {
    private final AdvogadoRepository repository;

    public AdvogadoService(AdvogadoRepository repository) {
        this.repository = repository;
    }

    public AdvogadoResponseDTO cadastrar(AdvogadoRequestDTO dto) {
        Advogado adv = new Advogado();
        adv.setNome(dto.nome());
        adv.setEmail(dto.email());
        adv.setSenha(dto.senha()); // criptografar depois
        adv.setOab(dto.oab());

        Advogado salvo = repository.save(adv);
        return new AdvogadoResponseDTO(salvo.getId(), salvo.getNome(), salvo.getEmail(), salvo.getOab());
    }
}

