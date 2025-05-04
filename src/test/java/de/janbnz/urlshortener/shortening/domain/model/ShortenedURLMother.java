package de.janbnz.urlshortener.shortening.domain.model;

import com.github.javafaker.Faker;
import de.janbnz.urlshortener.auth.domain.user.model.UserDtoMother;
import java.util.UUID;

public class ShortenedURLMother {

    private static final Faker FAKER = new Faker();

    public static ShortenedURL.ShortenedURLBuilder complete() {
        return new ShortenedURL.ShortenedURLBuilder()
                .id(UUID.randomUUID().toString())
                .originalUrl(UUID.randomUUID().toString())
                .redirectCount(0)
                .userId(UserDtoMother.complete().build().getId());
    }
}
