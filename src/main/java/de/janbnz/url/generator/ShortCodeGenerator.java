package de.janbnz.url.generator;

import java.util.Random;

public class ShortCodeGenerator {

    private static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 6;

    /**
     * Generates a short code
     *
     * @return a short code
     */
    public static String generateShortCode() {
        final StringBuilder sb = new StringBuilder(CODE_LENGTH);
        final Random random = new Random();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            char randomChar = CHARACTERS.charAt(index);
            sb.append(randomChar);
        }

        return sb.toString();
    }
}