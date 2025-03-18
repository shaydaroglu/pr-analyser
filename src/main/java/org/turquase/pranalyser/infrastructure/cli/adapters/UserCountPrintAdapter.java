package org.turquase.pranalyser.infrastructure.cli.adapters;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.turquase.pranalyser.domain.model.UserStatistic;

import java.util.List;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserCountPrintAdapter {
    private static final String FORMAT = "%-20s | %-17d | %-17f | %-17d";
    private static final String HEADER = String.format("%-20s | %-17s | %-17s | %-17s", "Contributor", "PR Count", "Avg. Comment", "Review Count");

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
