package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AbstractStrategyConfig;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Paths;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CliUtils {

    public static void validateArgs(String[] args) {
        if (args == null || args.length == 0) {
            throw new CliException("No arguments provided.", true);
        }
    }

    public static Strategy resolveStrategy(String name) {
        return Strategy.fromName(name).orElseThrow(() -> new CliException("Unknown strategy: " + name, true));
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractStrategyConfig> T buildStrategyConfig(String[] strategyArgs, Strategy strategy) {
        ParsedStrategy parsedStrategy = parseArgs(strategy.name().toLowerCase(), strategyArgs);
        StrategyArgs args = parsedStrategy.args();
        Class<T> configClass = (Class<T>) strategy.getConfigClass();

        return loadConfigOrUseArgs(parsedStrategy, configClass, () -> (T) strategy.buildConfig(args.source, args.destination, args.result));
    }

    public static ParsedStrategy parseArgs(String strategyName, String[] args) {
        StrategyArgs strategyArgs = new StrategyArgs();
        JCommander jc = JCommander.newBuilder()
                .addObject(strategyArgs)
                .programName("merge-structure-cli " + strategyName)
                .build();

        try {
            jc.parse(args);
            if (strategyArgs.help) {
                throw new CliException(null, jc);
            }
        } catch (ParameterException e) {
            throw new CliException("Error: " + e.getMessage(), jc);
        }

        return new ParsedStrategy(strategyArgs, jc);
    }

    public static <T extends AbstractStrategyConfig> T loadConfigOrUseArgs(
            ParsedStrategy parsedArgs,
            Class<T> configClass,
            Supplier<T> fallbackConfigSupplier
    ) {
        StrategyArgs args = parsedArgs.args();
        JCommander jc = parsedArgs.commander();

        if (args.config != null) {
            if (isInvalidPath(args.config)) {
                throw new CliException("Invalid value for --config: " + args.config, jc);
            }

            return FileUtils.loadFileToObject(Paths.get(args.config), configClass);
        }

        if (Stream.of(args.source, args.destination, args.result).anyMatch(CliUtils::isInvalidPath)) {
            throw new CliException("Missing or invalid required arguments.", jc);
        }

        return fallbackConfigSupplier.get();
    }

    public static boolean isInvalidPath(String path) {
        return path == null || path.isBlank() || path.startsWith("--");
    }

    public static void exitWithError(CliException e) {
        if (e.getMessage() != null) {
            System.err.println(e.getMessage());
        }
        if (e.shouldShowUsage()) {
            if (e.getCommander() != null) {
                e.getCommander().usage();
            } else {
                printUsage();
            }
        }
        System.exit(1);
    }

    public static void printUsage() {
        System.out.println("""
            
            Usage:
              java -jar merge-structure-cli.jar <strategy> [options]

            Example:
              java -jar merge-structure-cli.jar append --config config.yaml
              OR
              java -jar merge-structure-cli.jar append --source source.yaml --destination dest.yaml --result output.yaml

            Supported strategies: append, merge, replace, extend
            Supported formats: JSON (.json), YAML (.yaml or .yml)

            Use '--help' to display help message.
            """);
    }
}
