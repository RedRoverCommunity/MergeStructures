package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AppendStrategyConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
    @ValueSource(strings = { "json", "yaml" })
    void testLoadConfigOrUseArgsWithFallback(String format) {
        StrategyArgs mockArgs = new StrategyArgs();
        mockArgs.source      = "src." + format;
        mockArgs.destination = "dst." + format;
        mockArgs.result      = "out." + format;
        ParsedStrategy parsed = new ParsedStrategy(mockArgs, null);

        AppendStrategyConfig expected =
                new AppendStrategyConfig(Strategy.APPEND,
                        "src." + format,
                        "dst." + format,
                        "out." + format);

        AppendStrategyConfig actual = CliUtils.loadConfigOrUseArgs(
                parsed,
                AppendStrategyConfig.class,
                List.of(mockArgs.source,
                        mockArgs.destination,
                        mockArgs.result),
                () -> expected
        );

        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = { "json", "yaml" })
    void testLoadConfigOrUseArgsWithConfig(String format) {
        try (TempFile configFile = new TempFile("config", "." + format)) {
            if ("json".equals(format)) {
                configFile.write("""
                    {
                      "strategy": "append",
                      "sourceFile": "src.json",
                      "destinationFile": "dest.json",
                      "resultFile": "out.json"
                    }
                    """);
            } else {
                configFile.write("""
                    strategy: append
                    sourceFile: src.yaml
                    destinationFile: dest.yaml
                    resultFile: out.yaml
                    """);
            }

            StrategyArgs strategyArgs = new StrategyArgs();
            strategyArgs.config = configFile.getPath().toString();

            ParsedStrategy parsed = new ParsedStrategy(strategyArgs, JCommander.newBuilder().addObject(strategyArgs).build());

            AppendStrategyConfig config = CliUtils.loadConfigOrUseArgs(
                    parsed,
                    AppendStrategyConfig.class,
                    List.of(
                            "src." + format,
                            "dest." + format,
                            "out." + format
                    ),
                    () -> new AppendStrategyConfig(
                            Strategy.APPEND,
                            "src." + format,
                            "dest." + format,
                            "out." + format
                    )
            );

            assertNotNull(config);
            assertEquals("src." + format, config.getSourceFile());
            assertEquals("dest." + format, config.getDestinationFile());
            assertEquals("out." + format, config.getResultFile());
        }
    }

    @ParameterizedTest
    @ValueSource(strings = { "json", "yaml" })
    void testLoadConfigOrUseArgsWithArgsOnly(String format) {
        StrategyArgs strategyArgs = new StrategyArgs();
        strategyArgs.source      = "src." + format;
        strategyArgs.destination = "dest." + format;
        strategyArgs.result      = "out." + format;

        ParsedStrategy parsed = new ParsedStrategy(strategyArgs, JCommander.newBuilder().addObject(strategyArgs).build());

        AppendStrategyConfig config = CliUtils.loadConfigOrUseArgs(
                parsed,
                AppendStrategyConfig.class,
                List.of(
                        strategyArgs.source,
                        strategyArgs.destination,
                        strategyArgs.result
                ),
                () -> new AppendStrategyConfig(
                        Strategy.APPEND,
                        strategyArgs.source,
                        strategyArgs.destination,
                        strategyArgs.result
                )
        );

        assertNotNull(config);
        assertEquals("src." + format, config.getSourceFile());
        assertEquals("dest." + format, config.getDestinationFile());
        assertEquals("out." + format, config.getResultFile());
    }
}