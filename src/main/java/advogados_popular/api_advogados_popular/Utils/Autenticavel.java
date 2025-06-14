package advogados_popular.api_advogados_popular.Utils;

import advogados_popular.api_advogados_popular.DTOs.utils.Role;
import org.springframework.security.core.userdetails.UserDetails;

public interface Autenticavel extends UserDetails {
    String getEmail();
    String getSenha();
    Role getRole();
}
