package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.util.SupportedExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import community.redrover.merge.testutils.TempFile;

public class CliUtilsTest {

    @Test
    void testIsInvalidPathNull() {
        assertTrue(CliUtils.isInvalidPath(null));
    }

    @Test
    void testIsInvalidPathBlank() {
        assertTrue(CliUtils.isInvalidPath("   "));
    }

    @Test
    void testIsInvalidPathStartsWithDash() {
        assertTrue(CliUtils.isInvalidPath("--source"));
    }

    @Test
    void testIsInvalidPathValid() {
        assertFalse(CliUtils.isInvalidPath("valid/path.yaml"));
    }

    @Test
    void testParseArgsHelpFlagThrowsCliException() {
        String[] args = {"--help"};

        CliException ex = assertThrows(CliException.class, () -> CliUtils.parseArgs("append", args));

        assertTrue(ex.shouldShowUsage());
        assertNull(ex.getMessage());
    }

    @Test
    void testParseArgsInvalidArgsThrowsCliException() {
        String[] args = {"--unknown"};

        CliException ex = assertThrows(CliException.class, () -> CliUtils.parseArgs("append", args));

        assertTrue(ex.shouldShowUsage());
        assertTrue(ex.getMessage().startsWith("Error:"));
    }

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testLoadConfigOrUseArgsWithFallback(SupportedExtension ext) {
        String format = ext.getValue();
        StrategyArgs mockArgs = new StrategyArgs();
        mockArgs.source = "src." + format;
        mockArgs.destination = "dst." + format;
        mockArgs.result = "out." + format;
        ParsedStrategy parsed = new ParsedStrategy(mockArgs, null);

        AppendStrategyConfig actual = CliUtils.loadConfigOrUseArgs(
                parsed,
                AppendStrategyConfig.class,
                List.of(mockArgs.source, mockArgs.destination, mockArgs.result),
                () -> new AppendStrategyConfig(
                        Strategy.APPEND,
                        "src." + format,
                        "dst." + format,
                        "out." + format
                )
        );

        assertAll(
                () -> assertEquals("src." + format, actual.getSourceFile()),
                () -> assertEquals("dst." + format, actual.getDestinationFile()),
                () -> assertEquals("out." + format, actual.getResultFile())
        );
    }

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testLoadConfigOrUseArgsWithConfig(SupportedExtension ext) {
        String format = ext.getValue();
        try (TempFile configFile = new TempFile("config", "." + format)) {
            if (ext == SupportedExtension.JSON) {
                configFile.write(String.format(
                        "{\n" +
                                "  \"strategy\": \"append\",\n" +
                                "  \"sourceFile\": \"src.%1$s\",\n" +
                                "  \"destinationFile\": \"dest.%1$s\",\n" +
                                "  \"resultFile\": \"out.%1$s\"\n" +
                                "}", format
                ));
            } else {
                configFile.write(String.format(
                        "strategy: append\n" +
                                "sourceFile: src.%1$s\n" +
                                "destinationFile: dest.%1$s\n" +
                                "resultFile: out.%1$s\n", format
                ));
            }

            StrategyArgs strategyArgs = new StrategyArgs();
            strategyArgs.config = configFile.getPath().toString();

            ParsedStrategy parsed = new ParsedStrategy(
                    strategyArgs,
                    JCommander.newBuilder().addObject(strategyArgs).build()
            );

            AppendStrategyConfig actual = CliUtils.loadConfigOrUseArgs(
                    parsed,
                    AppendStrategyConfig.class,
                    List.of("src." + format, "dest." + format, "out." + format),
                    () -> new AppendStrategyConfig(
                            Strategy.APPEND,
                            "src." + format,
                            "dest." + format,
                            "out." + format
                    )
            );

            assertAll(
                    () -> assertEquals("src." + format, actual.getSourceFile()),
                    () -> assertEquals("dest." + format, actual.getDestinationFile()),
                    () -> assertEquals("out." + format, actual.getResultFile())
            );
        }
    }

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testLoadConfigOrUseArgsWithArgsOnly(SupportedExtension ext) {
        String format = ext.getValue();
        StrategyArgs strategyArgs = new StrategyArgs();
        strategyArgs.source = "src." + format;
        strategyArgs.destination = "dest." + format;
        strategyArgs.result = "out." + format;

        ParsedStrategy parsed = new ParsedStrategy(
                strategyArgs,
                JCommander.newBuilder().addObject(strategyArgs).build()
        );

        AppendStrategyConfig actual = CliUtils.loadConfigOrUseArgs(
                parsed,
                AppendStrategyConfig.class,
                List.of(strategyArgs.source, strategyArgs.destination, strategyArgs.result),
                () -> new AppendStrategyConfig(
                        Strategy.APPEND,
                        strategyArgs.source,
                        strategyArgs.destination,
                        strategyArgs.result
                )
        );

        assertAll(
                () -> assertEquals("src." + format, actual.getSourceFile()),
                () -> assertEquals("dest." + format, actual.getDestinationFile()),
                () -> assertEquals("out." + format, actual.getResultFile())
        );
    }
}