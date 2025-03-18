package org.turquase.pranalyser.port.in;

import org.turquase.pranalyser.domain.model.UserStatistic;

import java.time.LocalDate;
import java.util.List;

public interface PrStatisticCalculatorPort {
    List<UserStatistic> calculateUserPrStatistics(String repoName, String owner, String accessToken, LocalDate startDate, LocalDate endDate);
}
