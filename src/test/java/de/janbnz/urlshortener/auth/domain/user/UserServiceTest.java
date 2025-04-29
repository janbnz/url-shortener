package de.janbnz.urlshortener.auth.domain.user;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.janbnz.urlshortener.auth.domain.user.model.UserDto;
import de.janbnz.urlshortener.auth.domain.user.model.UserDtoMother;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    @Test
    void shouldRegisterUser() {
        // given
        UserDto user = UserDtoMother.complete().build();
        when(userRepository.save(any())).thenReturn(user);
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        // when
        userService.registerUser(user);

        // then
        verify(userRepository).save(any());
    }

    @Test
    void shouldNotRegisterUserWhenUserAlreadyExists() {
        // given
        UserDto user = UserDtoMother.complete().build();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // when & then
        assertThrows(
                ResponseStatusException.class, () -> userService.registerUser(user), "User already exists");
    }

    @Test
    void shouldFindUserByUsername() {
        // given
        UserDto user = UserDtoMother.complete().build();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        // when
        UserDto foundUser = userService.findUserByUsername(user.getUsername());

        // then
        assertThat(foundUser).usingRecursiveComparison().isEqualTo(user);
    }

    @Test
    void shouldNotFindUserByUsernameWhenUserDoesNotExist() {
        // given
        String username = "nonexistentuser";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        // when & then
        assertThrows(
                ResponseStatusException.class,
                () -> userService.findUserByUsername(username),
                "User not found");
    }
}
