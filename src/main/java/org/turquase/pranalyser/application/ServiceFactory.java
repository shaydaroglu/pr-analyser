package org.turquase.pranalyser.application;

import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.turquase.pranalyser.domain.services.PrStatisticCalculator;
import org.turquase.pranalyser.infrastructure.github.GithubService;
import org.turquase.pranalyser.port.in.PrStatisticCalculatorPort;
import org.turquase.pranalyser.port.out.GitRepoServicePort;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ServiceFactory {
    public static PrStatisticCalculatorPort createPrStatisticCalculatorPort() {
        return new PrStatisticCalculator();
    }

    public static GitRepoServicePort createGithubServicePort(String authToken) {
        if(StringUtils.isBlank(authToken)) {
            throw new IllegalArgumentException("Auth token cannot be null or empty.");
        }

        return new GithubService(authToken);
    }
}
