package de.janbnz.url.generator;

import java.util.function.Supplier;

public class AlphaNumericCodeGenerator implements CodeGeneratorStrategy {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 6;
    private final Supplier<Integer> randomIndexSupplier;

    public AlphaNumericCodeGenerator() {
        this.randomIndexSupplier = () -> (int) (Math.random() * CHARACTERS.length());
    }

    @Override
    public String generateCode() {
        final StringBuilder sb = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = randomIndexSupplier.get();
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }
        return sb.toString();
    }
}