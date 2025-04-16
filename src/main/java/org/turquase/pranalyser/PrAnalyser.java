package org.turquase.pranalyser;

import lombok.extern.slf4j.Slf4j;
import org.turquase.pranalyser.infrastructure.cli.commands.PrAnalyserCommand;
import picocli.CommandLine;


@Slf4j
public class PrAnalyser {
    public static void main(String[] args) {
        PrAnalyserCommand analyserCommand = new PrAnalyserCommand();

        int exitCode = new CommandLine(analyserCommand).execute(args);
        System.exit(exitCode);
    }
}
