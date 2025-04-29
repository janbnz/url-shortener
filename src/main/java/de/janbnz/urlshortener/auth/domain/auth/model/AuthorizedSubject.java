package de.janbnz.urlshortener.auth.domain.auth.model;

import de.janbnz.urlshortener.auth.domain.user.model.UserRole;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class AuthorizedSubject {

    private final UUID id;
    private final String username;
    private final UserRole role;
}
