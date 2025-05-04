package de.janbnz.urlshortener.auth.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import de.janbnz.urlshortener.auth.domain.auth.model.AuthorizedSubject;
import de.janbnz.urlshortener.auth.domain.user.model.UserRole;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class SecurityServiceTest {

    private final SecurityService securityService = new SecurityService();

    @Test
    void shouldReturnNullWhenAuthenticationIsNull() {
        // given
        SecurityContextHolder.clearContext();

        // when
        AuthorizedSubject result = securityService.getCurrentAuthorizedSubject();

        // then
        assertNull(result);
    }

    @Test
    void shouldReturnCorrectUser() {
        // given
        UUID uuid = UUID.randomUUID();
        String username = "testUser";
        String role = UserRole.USER.name();

        Jwt jwt = mock(Jwt.class);
        when(jwt.getSubject()).thenReturn(uuid.toString());
        when(jwt.getClaimAsString("username")).thenReturn(username);
        when(jwt.getClaimAsString("role")).thenReturn(role);

        Authentication authentication = new UsernamePasswordAuthenticationToken(jwt, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // when
        AuthorizedSubject result = securityService.getCurrentAuthorizedSubject();

        // then
        assertNotNull(result);
        assertEquals(uuid, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals(UserRole.USER, result.getRole());
    }
}
