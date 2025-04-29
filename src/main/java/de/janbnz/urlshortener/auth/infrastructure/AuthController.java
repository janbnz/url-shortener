package de.janbnz.urlshortener.auth.infrastructure;

import de.janbnz.urlshortener.auth.domain.auth.AuthService;
import de.janbnz.urlshortener.auth.domain.user.UserService;
import de.janbnz.urlshortener.auth.domain.user.model.RegisterRequest;
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
    public UserDto register(@RequestBody RegisterRequest request) {
        return userService.registerUser(
                UserDto.builder().username(request.username()).password(request.password()).build());
    }
}
