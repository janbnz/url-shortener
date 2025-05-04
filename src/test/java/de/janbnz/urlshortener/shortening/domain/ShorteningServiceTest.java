package de.janbnz.urlshortener.shortening.domain;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import de.janbnz.urlshortener.shortening.domain.model.ShortenedURL;
import de.janbnz.urlshortener.shortening.domain.model.ShortenedURLMother;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShorteningServiceTest {

    @Mock private ShorteningRepository shorteningRepository;

    @InjectMocks private ShorteningService shorteningService;

    @Test
    void shouldNotRedirect() {
        // given
        ShortenedURL shortUrl = ShortenedURLMother.complete().build();
        when(shorteningRepository.findById(shortUrl.getId())).thenReturn(Optional.empty());

        // when
        assertThrows(
                RuntimeException.class,
                () -> shorteningService.redirect(shortUrl.getId()),
                "Shortened URL not found");
    }
}
