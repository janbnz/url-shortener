package de.janbnz.urlshortener.auth.domain.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import de.janbnz.urlshortener.auth.domain.JwtUtil;
import de.janbnz.urlshortener.auth.domain.user.UserService;
import de.janbnz.urlshortener.auth.domain.user.model.UserDto;
import de.janbnz.urlshortener.auth.domain.user.model.UserDtoMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserService userService;

    @Mock private PasswordEncoder passwordEncoder;

    @Mock private JwtUtil jwtUtil;

    @InjectMocks private AuthService authService;

    @Test
    void shouldLogin() {
        // given
        String username = "testUser";
        String password = "testPassword";
        UserDto user = UserDto.builder().build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(any())).thenReturn("token");

        // when
        String token = authService.login(username, password);

        // then
        assertEquals("token", token);
    }

    @Test
    void shouldNotLoginWithInvalidCredentials() {
        // given
        String username = "testUser";
        String password = "wrongPassword";
        UserDto user = UserDtoMother.complete().build();

        when(userService.findUserByUsername(username)).thenReturn(user);
        when(passwordEncoder.matches(password, user.getPassword())).thenReturn(false);

        // when & then
        assertThrows(
                ResponseStatusException.class,
                () -> authService.login(username, password),
                "Invalid credentials");
    }
}
