package org.turquase.pranalyser.domain.services;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.turquase.pranalyser.ReflectionUtils;
import org.turquase.pranalyser.domain.model.UserStatistic;
import org.turquase.pranalyser.infrastructure.github.GithubService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrStatisticCalculatorTest {

    @Test
    @SneakyThrows
    void shouldReturnEmptyListWhenNoPRsFound() {
        GithubService mockRepoService = mock(GithubService.class);
        when(mockRepoService.getPullRequests(anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        PrStatisticCalculator calculator = new PrStatisticCalculator();
        ReflectionUtils.setField(calculator, "gitRepoServicePort", mockRepoService);
        List<UserStatistic> stats = calculator.calculateUserPrStatistics("repo", "user", "token", LocalDate.now().minusDays(7), LocalDate.now());

        assertTrue(stats.isEmpty());
    }

//    @Test
//    void shouldReturnCorrectStatisticsForSingleUser() {
//        GitRepoServicePort mockRepoService = mock(GitRepoServicePort.class);
//        when(mockRepoService.getPullRequests(anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
//                .thenReturn(List.of(new PR("user1", 5), new PR("user1", 3)));
//
//        PrStatisticCalculator calculator = new PrStatisticCalculator(mockRepoService);
//        List<UserStatistic> stats = calculator.calculateUserPrStatistics("repo", "user", "token", LocalDate.now().minusDays(7), LocalDate.now());
//
//        assertEquals(1, stats.size());
//        assertEquals("user1", stats.get(0).getUsername());
//        assertEquals(8, stats.get(0).getPrCount());
//    }
//
//    @Test
//    void shouldReturnCorrectStatisticsForMultipleUsers() {
//        GitRepoServicePort mockRepoService = mock(GitRepoServicePort.class);
//        when(mockRepoService.getPullRequests(anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
//                .thenReturn(List.of(new PR("user1", 5), new PR("user2", 3)));
//
//        PrStatisticCalculator calculator = new PrStatisticCalculator(mockRepoService);
//        List<UserStatistic> stats = calculator.calculateUserPrStatistics("repo", "user", "token", LocalDate.now().minusDays(7), LocalDate.now());
//
//        assertEquals(2, stats.size());
//        assertEquals("user1", stats.get(0).getUsername());
//        assertEquals(5, stats.get(0).getPrCount());
//        assertEquals("user2", stats.get(1).getUsername());
//        assertEquals(3, stats.get(1).getPrCount());
//    }
//
//    @Test
//    void shouldHandleNullPRList() {
//        GitRepoServicePort mockRepoService = mock(GitRepoServicePort.class);
//        when(mockRepoService.getPullRequests(anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
//                .thenReturn(null);
//
//        PrStatisticCalculator calculator = new PrStatisticCalculator(mockRepoService);
//        List<UserStatistic> stats = calculator.calculateUserPrStatistics("repo", "user", "token", LocalDate.now().minusDays(7), LocalDate.now());
//
//        assertTrue(stats.isEmpty());
//    }
//
//    @Test
//    void shouldThrowExceptionWhenRepoServiceFails() {
//        GitRepoServicePort mockRepoService = mock(GitRepoServicePort.class);
//        when(mockRepoService.getPullRequests(anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
//                .thenThrow(new RuntimeException("Service failure"));
//
//        PrStatisticCalculator calculator = new PrStatisticCalculator(mockRepoService);
//
//        assertThrows(RuntimeException.class, () -> {
//            calculator.calculateUserPrStatistics("repo", "user", "token", LocalDate.now().minusDays(7), LocalDate.now());
//        });
//    }
}
