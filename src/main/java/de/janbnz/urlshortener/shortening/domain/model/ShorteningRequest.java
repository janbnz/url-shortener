package de.janbnz.urlshortener.shortening.domain.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShorteningRequest {
    private String url;
}
