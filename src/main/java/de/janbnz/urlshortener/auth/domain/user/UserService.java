package de.janbnz.urlshortener.auth.domain.user;

import de.janbnz.urlshortener.auth.domain.user.model.UserDto;
import de.janbnz.urlshortener.auth.domain.user.model.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto registerUser(UserDto user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User already exists");
        }

        user =
                user.toBuilder()
                        .role(UserRole.USER)
                        .password(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()))
                        .build();
        return userRepository.save(user);
    }

    public UserDto findUserByUsername(String username) {
        return userRepository
                .findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
