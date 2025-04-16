package org.turquase.pranalyser.infrastructure.cli.commands;

import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.mockito.MockedStatic;
import org.turquase.pranalyser.application.ServiceFactory;
import org.turquase.pranalyser.domain.model.UserStatistic;
import org.turquase.pranalyser.port.in.PrStatisticCalculatorPort;
import picocli.CommandLine;
import testutils.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class PrAnalyserCommandTest {

    @Test
    void shouldReturnZeroWhenNoUserStatisticsFound() {
        String accessToken = "token";
        PrStatisticCalculatorPort mockPort = mock(PrStatisticCalculatorPort.class);

        try (MockedStatic<ServiceFactory> mockedStatic = mockStatic(ServiceFactory.class)) {
            mockedStatic.when(() -> ServiceFactory.createPrStatisticCalculatorPort(accessToken))
                    .thenReturn(mockPort);
            when(mockPort.calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());

            PrAnalyserCommand command = new PrAnalyserCommand();
            CommandLine cmd = new CommandLine(command);
            int exitCode = cmd.execute("-r", "repo", "-o", "owner", "-t", accessToken, "-sd", "2025-03-16", "-ed", "2025-03-18");

            assertEquals(0, exitCode);
            verify(mockPort, times(1)).calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class));
        }
    }

    @Test
    void shouldReturnOneWhenExceptionThrown() {
        String accessToken = "token";
        PrStatisticCalculatorPort mockPort = mock(PrStatisticCalculatorPort.class);

        try (MockedStatic<ServiceFactory> mockedStatic = mockStatic(ServiceFactory.class)) {
            mockedStatic.when(() -> ServiceFactory.createPrStatisticCalculatorPort(accessToken))
                    .thenReturn(mockPort);
            when(mockPort.calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                    .thenThrow(new RuntimeException("Error"));

            PrAnalyserCommand command = new PrAnalyserCommand();

            CommandLine cmd = new CommandLine(command);
            int exitCode = cmd.execute("-r", "repo", "-o", "owner", "-t", accessToken, "-sd", "2025-03-16", "-ed", "2025-03-18");

            assertEquals(1, exitCode);
            verify(mockPort, times(1)).calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class));
        }
    }

    @Test
    void shouldReturnZeroWhenUserStatisticsFetchedSuccessfully() {
        String accessToken = "token";
        PrStatisticCalculatorPort mockPort = mock(PrStatisticCalculatorPort.class);

        try (MockedStatic<ServiceFactory> mockedStatic = mockStatic(ServiceFactory.class)) {
            mockedStatic.when(() -> ServiceFactory.createPrStatisticCalculatorPort(accessToken))
                    .thenReturn(mockPort);
            List<UserStatistic> userStatistics = List.of(new UserStatistic("user1", 5L, 1.1, 1L));
            when(mockPort.calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class)))
                    .thenReturn(userStatistics);

            PrAnalyserCommand command = new PrAnalyserCommand();
            ReflectionTestUtils.setField(command, "prStatisticCalculatorPort", mockPort);

            CommandLine cmd = new CommandLine(command);
            int exitCode = cmd.execute("-r", "repo", "-o", "owner", "-t", accessToken, "-sd", "2025-03-16", "-ed", "2025-03-18");

            assertEquals(0, exitCode);
            verify(mockPort, times(1)).calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class));
        }
    }

    @Test
    void shouldThrowErrorWhenArgumentMissing() {
        String accessToken = "token";
        PrStatisticCalculatorPort mockPort = mock(PrStatisticCalculatorPort.class);

        try (MockedStatic<ServiceFactory> mockedStatic = mockStatic(ServiceFactory.class)) {
            mockedStatic.when(() -> ServiceFactory.createPrStatisticCalculatorPort(accessToken))
                    .thenReturn(mockPort);

            PrAnalyserCommand command = new PrAnalyserCommand();
            ReflectionTestUtils.setField(command, "prStatisticCalculatorPort", mockPort);

            CommandLine cmd = new CommandLine(command);
            int exitCode = cmd.execute("-o", "owner", "-t", "token", "-sd", "2025-03-16", "-ed", "2025-03-18");
            int exitCode2 = cmd.execute("-r", "repo", "-t", "token", "-sd", "2025-03-18", "-ed", "2025-03-18");
            int exitCode3 = cmd.execute("-r", "repo", "-o", "owner", "-sd", "2025-03-18", "-ed", "2025-03-18");
            int exitCode4 = cmd.execute("-r", "repo", "-o", "owner", "-t", "token", "-ed", "2025-03-18");
            int exitCode5 = cmd.execute("-r", "repo", "-o", "owner", "-t", "token", "-sd", "2025-03-18");
            int exitCode6 = cmd.execute("-");

            assertEquals(2, exitCode);
            assertEquals(2, exitCode2);
            assertEquals(2, exitCode3);
            assertEquals(2, exitCode4);
            assertEquals(2, exitCode5);
            assertEquals(2, exitCode6);
            verify(mockPort, never()).calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class));
        }
    }

    @Test
    void shouldNotThrowErrorWhenHelpArgumentPassed() {
        String accessToken = "token";
        PrStatisticCalculatorPort mockPort = mock(PrStatisticCalculatorPort.class);

        try (MockedStatic<ServiceFactory> mockedStatic = mockStatic(ServiceFactory.class)) {
            mockedStatic.when(() -> ServiceFactory.createPrStatisticCalculatorPort(accessToken))
                    .thenReturn(mockPort);

            PrAnalyserCommand command = new PrAnalyserCommand();
            ReflectionTestUtils.setField(command, "prStatisticCalculatorPort", mockPort);

            CommandLine cmd = new CommandLine(command);
            int exitCode = cmd.execute("-h");
            int exitCode2 = cmd.execute("--help");

            assertEquals(0, exitCode);
            assertEquals(0, exitCode2);
            verify(mockPort, never()).calculateUserPrStatistics(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class));
        }
    }
}
