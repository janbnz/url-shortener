package de.janbnz.urlshortener.shortening.domain.model;

import de.janbnz.urlshortener.shortening.domain.code.impl.AlphaNumericCodeGenerator;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class ShortenedURL {

    private static final AlphaNumericCodeGenerator codeGenerator = new AlphaNumericCodeGenerator();

    @Id
    private String id;

    private String originalUrl;
    private int redirectCount;

    private UUID userId;

    @PrePersist
    private void generateId() {
        if (this.id == null || this.id.isEmpty()) {
            this.id = codeGenerator.generateCode();
        }
    }
}
