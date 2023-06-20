package de.janbnz.url.generator;

public class ShortCodeGenerator {
    private final CodeGeneratorStrategy codeGenerator;

    public ShortCodeGenerator(CodeGeneratorStrategy codeGenerator) {
        this.codeGenerator = codeGenerator;
    }

    /**
     * Generates a short code
     *
     * @return a short code
     */
    public String generateShortCode() {
        return codeGenerator.generateCode();
    }
}