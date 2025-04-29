package de.janbnz.urlshortener.shortening.domain;

import de.janbnz.urlshortener.auth.domain.auth.model.AuthorizedSubject;
import de.janbnz.urlshortener.config.Role;
import de.janbnz.urlshortener.config.SecurityConfig;
import de.janbnz.urlshortener.shortening.domain.model.ShortenedURL;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ShorteningService {

    private final ShorteningRepository shorteningRepository;

    @RolesAllowed(Role.USER)
    public ShortenedURL shorten(String originalUrl) {
        final AuthorizedSubject authorizedSubject = SecurityConfig.getCurrentAuthorizedSubject();

        if (authorizedSubject == null) {
            throw new RuntimeException("Authorized subject not set");
        }

        final ShortenedURL url =
                ShortenedURL.builder()
                        .redirectCount(0)
                        .originalUrl(originalUrl)
                        .userId(authorizedSubject.getId())
                        .build();

        return shorteningRepository.save(url);
    }

    public ShortenedURL getInformation(String id) {
        return shorteningRepository
                .findById(id)
                .orElseThrow(
                        () ->
                                new ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find shortened URL"));
    }

    public ShortenedURL redirect(String id) {
        ShortenedURL url = getInformation(id);

        return shorteningRepository.save(
                url.toBuilder().redirectCount(url.getRedirectCount() + 1).build());
    }
}
