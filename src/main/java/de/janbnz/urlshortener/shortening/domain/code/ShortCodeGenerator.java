package de.janbnz.urlshortener.shortening.domain.code;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ShortCodeGenerator {

    private final CodeGeneratorStrategy strategy;

    public String generateShortCode() {
        return strategy.generateCode();
    }
}
