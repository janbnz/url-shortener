package de.janbnz.url.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Properties;

public class ServiceConfig {

    private final Properties properties;

    public ServiceConfig() {
        this.properties = new Properties();
        this.load();
    }

    public Properties getProperties() {
        return properties;
    }

    private void load() {
        final String configFileName = "config.properties";
        Path configPath = Path.of(configFileName);

        if (!Files.exists(configPath)) {
            this.copyFileFromResources(configFileName, configPath);
        }

        try (InputStream input = Files.newInputStream(configPath)) {
            this.properties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyFileFromResources(String configFileName, Path configPath) {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream(configFileName)) {
            if (input == null) {
                throw new RuntimeException("Config file '" + configFileName + "' not found in resources folder.");
            }
            Files.copy(input, configPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}