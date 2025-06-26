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
    void executeStrategyInvokesExecutor(SupportedExtension ext) {
        String format = ext.getValue();
        final boolean[] ran = {false};

        CliUtils.executeStrategy(
                "append",
                new String[]{
                        "--source", "src." + format,
                        "--destination", "dst." + format,
                        "--result", "out." + format
                },
                AbstractStrategyConfig.class,
                strategyArgs -> {
                    assertEquals("src." + format, strategyArgs.source);
                    assertEquals("dst." + format, strategyArgs.destination);
                    assertEquals("out." + format, strategyArgs.result);
                    return new AbstractStrategyConfig() {};
                },
                config -> {
                    assertNotNull(config);
                    ran[0] = true;
                }
        );

        assertTrue(ran[0], "Executor should have been called for format: " + format);
    }

    @Test
    void executeStrategyWithHelpFlagThrowsCliExceptionBeforeExecutor() {
        CliException exception = assertThrows(
                CliException.class,
                () -> CliUtils.executeStrategy(
                        "append",
                        new String[]{"--help"},
                        AbstractStrategyConfig.class,
                        strategyArgs -> fail("fallbackFactory should not be called when --help is present"),
                        config -> fail("executor should not be called when --help is present")
                )
        );

        assertTrue(exception.shouldShowUsage(), "Help flag should set showUsage=true");
        assertNull(exception.getMessage(), "Help flag should not set an error message");
    }

    @Test
    void executeStrategyMissingRequiredArgsThrowsCliException() {
        String[] args = {
                "--source", "src.yaml",
                "--destination", "dst.yaml"
        };

        CliException exception = assertThrows(
                CliException.class,
                () -> CliUtils.executeStrategy(
                        "append",
                        args,
                        AbstractStrategyConfig.class,
                        strategyArgs -> fail("fallbackFactory should not be invoked"),
                        config -> fail("executor should not be invoked")
                )
        );

        assertTrue(exception.shouldShowUsage(), "Missing args should set showUsage=true");
        assertEquals("Missing or invalid required arguments.",
                exception.getMessage(), "Exception message must indicate missing/invalid arguments");
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
        ParsedStrategy parsedArgs = new ParsedStrategy(mockArgs, null);

        AbstractStrategyConfig config = CliUtils.loadConfigOrUseArgs(
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
        AppendStrategyConfig actual = (AppendStrategyConfig) config;

        assertAll(
                () -> assertEquals(mockArgs.source, actual.getSourceFile()),
                () -> assertEquals(mockArgs.destination, actual.getDestinationFile()),
                () -> assertEquals(mockArgs.result, actual.getResultFile())
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

            AbstractStrategyConfig config = CliUtils.loadConfigOrUseArgs(
                    parsedArgs,
                    AppendStrategyConfig.class,
                    () -> expected
            );

            assertInstanceOf(AppendStrategyConfig.class, config);
            AppendStrategyConfig actual = (AppendStrategyConfig) config;

            assertAll(
                    () -> assertEquals(expected.getSourceFile(), actual.getSourceFile()),
                    () -> assertEquals(expected.getDestinationFile(), actual.getDestinationFile()),
                    () -> assertEquals(expected.getResultFile(), actual.getResultFile())
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

        AbstractStrategyConfig config = CliUtils.loadConfigOrUseArgs(
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
        AppendStrategyConfig actual = (AppendStrategyConfig) config;

        assertAll(
                () -> assertEquals("src." + format, actual.getSourceFile()),
                () -> assertEquals("dest." + format, actual.getDestinationFile()),
                () -> assertEquals("out." + format, actual.getResultFile())
        );
    }
}