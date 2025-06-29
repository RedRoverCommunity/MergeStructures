package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AbstractStrategyConfig;
import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.util.SupportedExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import static org.junit.jupiter.api.Assertions.*;
import community.redrover.merge.testutils.TempFile;

public class CliUtilsTest {

    @Test
    void validateArgsThrowsException() {
        String[] emptyArgs = {};

        CliException exception = assertThrows(CliException.class, () -> CliUtils.validateArgs(emptyArgs));

        assertAll(
                () -> assertEquals("No arguments provided.", exception.getMessage()),
                () -> assertTrue(exception.shouldShowUsage())
        );
    }

    @Test
    void resolveStrategyPositive() {
        assertEquals(Strategy.APPEND, CliUtils.resolveStrategy("APPEND"));
        assertEquals(Strategy.APPEND, CliUtils.resolveStrategy("Append"));
        assertEquals(Strategy.APPEND, CliUtils.resolveStrategy("append"));
    }

    @Test
    void resolveStrategyNegativeThrowsException() {
        CliException exception = assertThrows(CliException.class, () -> CliUtils.resolveStrategy("invalidStrategy"));

        assertAll(
                () -> assertEquals("Unknown strategy: invalidStrategy", exception.getMessage()),
                () -> assertTrue(exception.shouldShowUsage())
        );
    }

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testBuildStrategyConfigFromArgs(SupportedExtension ext) {
        String format = ext.getValue();
        String[] args = {
                "--source", "src." + format,
                "--destination", "dst." + format,
                "--result", "out." + format
        };

        Strategy strategy = Strategy.APPEND;
        AbstractStrategyConfig config = CliUtils.buildStrategyConfig(args, strategy);

        assertInstanceOf(AppendStrategyConfig.class, config);
        AppendStrategyConfig actual = (AppendStrategyConfig) config;

        assertEquals("src." + format, actual.getSourceFile());
        assertEquals("dst." + format, actual.getDestinationFile());
        assertEquals("out." + format, actual.getResultFile());
    }

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testBuildStrategyConfigFromFile(SupportedExtension ext) {
        String format = ext.getValue();
        Strategy strategy = Strategy.APPEND;

        AppendStrategyConfig expected = new AppendStrategyConfig(
                strategy,
                "src." + format,
                "dest." + format,
                "out." + format
        );

        try (TempFile configFile = new TempFile("config", "." + format)) {
            switch (ext) {
                case JSON -> configFile.write(String.format("""
                    {
                      "strategy": "append",
                      "sourceFile": "src.%1$s",
                      "destinationFile": "dest.%1$s",
                      "resultFile": "out.%1$s"
                    }""", format));

                case YAML, YML -> configFile.write(String.format("""
                    strategy: append
                    sourceFile: src.%1$s
                    destinationFile: dest.%1$s
                    resultFile: out.%1$s
                    """, format));

                default -> fail("Unsupported format: " + ext);
            }

            String[] args = {"--config", configFile.getPath().toString()};
            AbstractStrategyConfig config = CliUtils.buildStrategyConfig(args, strategy);

            assertInstanceOf(AppendStrategyConfig.class, config);
            AppendStrategyConfig actual = (AppendStrategyConfig) config;

            assertEquals(expected.getSourceFile(), actual.getSourceFile());
            assertEquals(expected.getDestinationFile(), actual.getDestinationFile());
            assertEquals(expected.getResultFile(), actual.getResultFile());
        }
    }

    @Test
    void testBuildStrategyConfigWithHelpFlagThrowsCliException() {
        String[] args = {"--help"};

        CliException exception = assertThrows(CliException.class, () -> CliUtils.buildStrategyConfig(args, Strategy.APPEND));

        assertTrue(exception.shouldShowUsage());
        assertNull(exception.getMessage());
    }

    @Test
    void testBuildStrategyConfigWithMissingArgsThrowsCliException() {
        String[] args = {
                "--source", "src.yaml",
                "--destination", "dst.yaml"
        };

        CliException exception = assertThrows(CliException.class, () -> CliUtils.buildStrategyConfig(args, Strategy.APPEND));

        assertEquals("Missing or invalid required arguments.", exception.getMessage());
        assertTrue(exception.shouldShowUsage());
    }

    @Test
    void testParseArgsHelpFlagThrowsCliException() {
        String[] args = {"--help"};

        CliException exception = assertThrows(CliException.class, () -> CliUtils.parseArgs("append", args));

        assertTrue(exception.shouldShowUsage());
        assertNull(exception.getMessage());
    }

    @Test
    void testParseArgsInvalidArgsThrowsCliException() {
        String[] args = {"--unknown"};

        CliException exception = assertThrows(CliException.class, () -> CliUtils.parseArgs("append", args));

        assertTrue(exception.shouldShowUsage());
        assertTrue(exception.getMessage().startsWith("Error:"));
    }

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

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testLoadConfigOrUseArgsWithFallback(SupportedExtension ext) {
        String format = ext.getValue();
        StrategyArgs mockArgs = new StrategyArgs();
        mockArgs.source = "src." + format;
        mockArgs.destination = "dst." + format;
        mockArgs.result = "out." + format;
        ParsedStrategy parsedArgs = new ParsedStrategy(mockArgs, null);

        AppendStrategyConfig config = CliUtils.loadConfigOrUseArgs(
                parsedArgs,
                AppendStrategyConfig.class,
                () -> new AppendStrategyConfig(
                        Strategy.APPEND,
                        mockArgs.source,
                        mockArgs.destination,
                        mockArgs.result
                )
        );

        assertInstanceOf(AppendStrategyConfig.class, config);

        assertAll(
                () -> assertEquals(mockArgs.source, config.getSourceFile()),
                () -> assertEquals(mockArgs.destination, config.getDestinationFile()),
                () -> assertEquals(mockArgs.result, config.getResultFile())
        );
    }

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testLoadConfigOrUseArgsWithConfig(SupportedExtension ext) {
        String format = ext.getValue();
        AppendStrategyConfig expected = new AppendStrategyConfig(
                Strategy.APPEND,
                "src." + format,
                "dest." + format,
                "out." + format
        );

        try (TempFile configFile = new TempFile("config", "." + format)) {
            switch (ext) {
                case JSON -> configFile.write(String.format(
                        """
                        {
                          "strategy": "append",
                          "sourceFile": "src.%1$s",
                          "destinationFile": "dest.%1$s",
                          "resultFile": "out.%1$s"
                        }""",
                        format
                ));
                case YAML, YML -> configFile.write(String.format(
                        """
                        strategy: append
                        sourceFile: src.%1$s
                        destinationFile: dest.%1$s
                        resultFile: out.%1$s
                        """,
                        format
                ));
                default -> fail("Unknown extension: " + ext);
            }

            StrategyArgs strategyArgs = new StrategyArgs();
            strategyArgs.config = configFile.getPath().toString();

            ParsedStrategy parsedArgs = new ParsedStrategy(strategyArgs, JCommander.newBuilder().addObject(strategyArgs).build());

            AppendStrategyConfig config = CliUtils.loadConfigOrUseArgs(parsedArgs, AppendStrategyConfig.class, () -> expected);

            assertInstanceOf(AppendStrategyConfig.class, config);

            assertAll(
                    () -> assertEquals(expected.getSourceFile(), config.getSourceFile()),
                    () -> assertEquals(expected.getDestinationFile(), config.getDestinationFile()),
                    () -> assertEquals(expected.getResultFile(), config.getResultFile())
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

        ParsedStrategy parsedArgs = new ParsedStrategy(strategyArgs, JCommander.newBuilder().addObject(strategyArgs).build());

        AppendStrategyConfig config = CliUtils.loadConfigOrUseArgs(
                parsedArgs,
                AppendStrategyConfig.class,
                () -> new AppendStrategyConfig(
                        Strategy.APPEND,
                        strategyArgs.source,
                        strategyArgs.destination,
                        strategyArgs.result
                )
        );

        assertInstanceOf(AppendStrategyConfig.class, config);

        assertAll(
                () -> assertEquals("src." + format, config.getSourceFile()),
                () -> assertEquals("dest." + format, config.getDestinationFile()),
                () -> assertEquals("out." + format, config.getResultFile())
        );
    }
}