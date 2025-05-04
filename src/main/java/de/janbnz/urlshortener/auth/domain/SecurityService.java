package de.janbnz.urlshortener.auth.domain;

import de.janbnz.urlshortener.auth.domain.auth.model.AuthorizedSubject;
import de.janbnz.urlshortener.auth.domain.user.model.UserRole;
import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public AuthorizedSubject getCurrentAuthorizedSubject() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        return AuthorizedSubject.builder()
                .id(UUID.fromString(jwt.getSubject()))
                .username(jwt.getClaimAsString("username"))
                .role(UserRole.valueOf(jwt.getClaimAsString("role")))
                .build();
    }
}
