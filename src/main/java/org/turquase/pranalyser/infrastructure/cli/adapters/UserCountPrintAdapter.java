package org.turquase.pranalyser.infrastructure.cli.adapters;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.turquase.pranalyser.domain.model.UserStatistic;

import java.util.List;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserCountPrintAdapter {
    private static final String FORMAT = "%-25s | %-20d | %-20f | %-20d";
    private static final String HEADER = String.format("%-25s | %-20s | %-20s | %-20s", "Contributor", "Raised PR Count", "Avg. Comment on PRs", "Reviewed PR Count");

    public static void printUserStatistics(List<UserStatistic> userCounts) {
        log.info(HEADER);
        for (UserStatistic userCount : userCounts) {
            log.info(formatUserCountString(userCount));
        }
    }

    private static String formatUserCountString(UserStatistic userStatistic) {
        return String.format(FORMAT, userStatistic.getContributor(), userStatistic.getRaisedPrCount(), userStatistic.getReceivedCommentPerPr(), userStatistic.getReviewedPrCount());
    }
}
