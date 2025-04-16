package org.turquase.pranalyser.application;

import org.junit.jupiter.api.Test;
import org.turquase.pranalyser.port.in.PrStatisticCalculatorPort;
import org.turquase.pranalyser.port.out.GitRepoServicePort;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceFactoryTest {
    @Test
    void shouldCreatePrStatisticCalculatorPort() {
        PrStatisticCalculatorPort port = ServiceFactory.createPrStatisticCalculatorPort("accessToken");
        assertNotNull(port);
    }

    @Test
    void shouldCreateGithubServicePortWithValidToken() {
        String validToken = "validToken";
        GitRepoServicePort port = ServiceFactory.createGithubServicePort(validToken);
        assertNotNull(port);
    }

    @Test
    void shouldThrowExceptionWhenCreatingGithubServicePortWithNullToken() {
        assertThrows(IllegalArgumentException.class, () ->
            ServiceFactory.createGithubServicePort(null)
        );
    }

    @Test
    void shouldThrowExceptionWhenCreatingGithubServicePortWithEmptyToken() {
        assertThrows(IllegalArgumentException.class, () ->
            ServiceFactory.createGithubServicePort("")
        );
    }
}
