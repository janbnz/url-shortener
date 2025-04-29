package de.janbnz.urlshortener.auth.domain.user.model;

import com.github.javafaker.Faker;
import java.util.UUID;

public class UserDtoMother {

    private static final Faker FAKER = new Faker();

    public static UserDto.UserDtoBuilder complete() {
        return new UserDto.UserDtoBuilder()
                .id(UUID.randomUUID())
                .username(FAKER.name().username())
                .password(FAKER.internet().password())
                .role(UserRole.USER);
    }
}
