package de.janbnz.urlshortener.auth.infrastructure;

import de.janbnz.urlshortener.auth.domain.auth.AuthService;
import de.janbnz.urlshortener.auth.domain.auth.model.TokenResponse;
import de.janbnz.urlshortener.auth.domain.user.UserService;
import de.janbnz.urlshortener.auth.domain.user.model.AuthorizationRequest;
import de.janbnz.urlshortener.auth.domain.user.model.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public UserDto register(@RequestBody AuthorizationRequest request) {
        return userService.registerUser(
                UserDto.builder().username(request.username()).password(request.password()).build());
    }

    @PostMapping("/login")
    public TokenResponse login(@RequestBody AuthorizationRequest request) {
        final String token = authService.login(request.username(), request.password());
        return TokenResponse.builder().token(token).build();
    }
}
