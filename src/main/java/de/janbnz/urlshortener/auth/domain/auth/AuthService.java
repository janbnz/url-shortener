package de.janbnz.urlshortener.auth.domain.auth;

import de.janbnz.urlshortener.auth.domain.JwtUtil;
import de.janbnz.urlshortener.auth.domain.user.UserService;
import de.janbnz.urlshortener.auth.domain.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public String login(String username, String password) {
        UserDto user = userService.findUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return jwtUtil.generateToken(user);
    }
}
