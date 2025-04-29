package de.janbnz.urlshortener.auth.domain.user;

import de.janbnz.urlshortener.auth.domain.user.model.UserDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserDto, UUID> {

    Optional<UserDto> findByUsername(String username);
}
