package org.turquase.pranalyser.infrastructure.config;

import lombok.extern.slf4j.Slf4j;
import okhttp3.logging.HttpLoggingInterceptor;

import java.io.IOException;
import java.util.Properties;

@Slf4j
public class ConfigLoader {

    private final Properties properties = new Properties();

    public ConfigLoader() {
        try (var input = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
            } else {
                log.error("Configuration file not found");
                throw new RuntimeException("Configuration file not found");
            }
        } catch (IOException e) {
            log.error("Failed to load configuration", e);
            throw new RuntimeException("Failed to load configuration", e);
        }
    }

    public String getGitHubApiBaseUrl() {
        return properties.getProperty("github.api.baseUrl", "https://api.github.com/");
    }

    public HttpLoggingInterceptor.Level getApiLoggingLevel() {
        String propertyKey = properties.getProperty("http.client.logging.level", "NONE");

        return switch (propertyKey) {
            case "BASIC" -> HttpLoggingInterceptor.Level.BASIC;
            case "HEADERS" -> HttpLoggingInterceptor.Level.HEADERS;
            case "BODY" -> HttpLoggingInterceptor.Level.BODY;
            default -> HttpLoggingInterceptor.Level.NONE;
        };
    }
}
