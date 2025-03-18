package org.turquase.pranalyser;

import org.junit.jupiter.api.Test;
import org.turquase.pranalyser.infrastructure.cli.commands.PrAnalyserCommand;
import picocli.CommandLine;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PrAnalyserTest {
    @Test
    void shouldExecuteCommandSuccessfully() {
        PrAnalyserCommand mockCommand = mock(PrAnalyserCommand.class);
        when(mockCommand.call()).thenReturn(0);
        CommandLine commandLine = new CommandLine(mockCommand);
        int exitCode = commandLine.execute("-r", "pr-analyser", "-u", "username", "-sd", "2025-03-16", "-ed", "2025-03-18", "-t", "Github_Access_Token");
        assertEquals(0, exitCode);
    }

    @Test
    void shouldHandleCommandExecutionFailure() {
        PrAnalyserCommand mockCommand = mock(PrAnalyserCommand.class);
        when(mockCommand.call()).thenReturn(1);
        CommandLine commandLine = new CommandLine(mockCommand);
        int exitCode = commandLine.execute("-r", "pr-analyser", "-u", "username", "-sd", "2025-03-16", "-ed", "2025-03-18", "-t", "Github_Access_Token");
        assertEquals(1, exitCode);
    }

    @Test
    void shouldMissingArgumentDoesntThrowException() {
        PrAnalyserCommand mockCommand = mock(PrAnalyserCommand.class);
        when(mockCommand.call()).thenReturn(0);
        CommandLine commandLine = new CommandLine(mockCommand);
        assertDoesNotThrow(() -> commandLine.execute("-r", "pr-analyser"));
    }
}
