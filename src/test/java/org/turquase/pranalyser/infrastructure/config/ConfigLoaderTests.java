package org.turquase.pranalyser.infrastructure.config;

import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConfigLoaderTests {

    @Test
    void shouldLoadGitHubApiBaseUrlFromProperties() {
        ConfigLoader configLoader = new ConfigLoader();
        String baseUrl = configLoader.getGitHubApiBaseUrl();
        assertEquals("https://api.github.com/", baseUrl);
    }

    @Test
    void shouldReturnNoneLoggingLevelWhenNotConfigured() {
        ConfigLoader configLoader = new ConfigLoader();
        HttpLoggingInterceptor.Level level = configLoader.getApiLoggingLevel();
        assertEquals(HttpLoggingInterceptor.Level.NONE, level);
    }
}
