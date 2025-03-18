package org.turquase.pranalyser.infrastructure.cli.commands;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.turquase.pranalyser.infrastructure.cli.adapters.UserCountPrintAdapter;
import org.turquase.pranalyser.port.in.PrStatisticCalculatorPort;
import org.turquase.pranalyser.domain.model.UserStatistic;
import picocli.CommandLine.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@Command(
        name = "pr-analyser",
        description = """
                Fetches GitHub user PR statistics for given dates for a repository.
                Example Usage:
                java -jar pr-analyser.jar --repository-name pr-analyser --user username --start-date 2025-03-16 --end-date 2025-03-18 --access-token Github_Access_Token
                java -jar pr-analyser.jar -r pr-analyser -u username -sd 2025-03-18 -ed 2025-03-18 -t Github_Access_Token
                """,
        version = "1.0",
        mixinStandardHelpOptions = true
)
@RequiredArgsConstructor
public class PrAnalyserCommand implements Callable<Integer> {

    @Option(names = {"-r", "--repository-name"}, description = "Name of the repository.", required = true)
    private String repositoryName;

    @Option(names = {"-u", "--user"}, description = "GitHub username.", required = true)
    private String user;

    @Option(names = {"-t", "--access-token"}, description = "GitHub access token.", required = true)
    private String accessToken;

    @Option(names = {"-sd", "--start-date"}, description = "Start date in yyyy-MM-dd format.", required = true)
    private LocalDate startDate;

    @Option(names = {"-ed", "--end-date"}, description = "End date in yyyy-MM-dd format.", required = true)
    private LocalDate endDate;

    private final PrStatisticCalculatorPort prStatisticCalculatorPort;

    @Override
    public Integer call() {

        log.info("Fetching user statistics for repository {} between dates [{}] - [{}].", repositoryName, startDate, endDate);
        List<UserStatistic> userCounts;

        try {
            userCounts = prStatisticCalculatorPort.calculateUserPrStatistics(
                    repositoryName,
                    user,
                    accessToken,
                    startDate,
                    endDate);
        } catch (RuntimeException e) {
            log.error("Failed to fetch user statistics.", e);
            return 1;
        }

        if(userCounts == null || userCounts.isEmpty()) {
            log.info("No user statistics found for the given dates.");
            return 0;
        }

        log.info("User statistics fetched successfully. Found {} users.", userCounts.size());
        UserCountPrintAdapter.printUserStatistics(userCounts);

        return 0;
    }
}
