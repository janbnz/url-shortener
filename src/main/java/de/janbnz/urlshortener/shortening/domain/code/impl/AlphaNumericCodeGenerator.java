package de.janbnz.urlshortener.shortening.domain.code.impl;

import de.janbnz.urlshortener.shortening.domain.code.CodeGeneratorStrategy;

import java.util.function.Supplier;

public class AlphaNumericCodeGenerator implements CodeGeneratorStrategy {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 5;
    private final Supplier<Integer> randomIndexSupplier = () -> (int) (Math.random() * CHARACTERS.length());

    @Override
    public String generateCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = randomIndexSupplier.get();
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }
}