package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import community.redrover.merge.model.config.AbstractStrategyConfig;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class CliUtils {

    public static void executeStrategy(
            String strategyName,
            String[] strategyArgs,
            Class<? extends AbstractStrategyConfig> configClass,
            Function<StrategyArgs, AbstractStrategyConfig> fallbackFactory,
            Consumer<AbstractStrategyConfig> executor
    ) {
        ParsedStrategy parsed = parseArgs(strategyName, strategyArgs);
        StrategyArgs args     = parsed.args();

        @SuppressWarnings("unchecked")
        Class<AbstractStrategyConfig> strategyConfig =
                (Class<AbstractStrategyConfig>)(Class<?>)configClass;

        AbstractStrategyConfig config = loadConfigOrUseArgs(
                parsed,
                strategyConfig,
                List.of(args.source, args.destination, args.result),
                () -> fallbackFactory.apply(args)
        );

        executor.accept(config);

        String cap = strategyName.substring(0,1).toUpperCase() + strategyName.substring(1);
        System.out.println(cap + " strategy completed successfully.");
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

    public static <T> T loadConfigOrUseArgs(
            ParsedStrategy parsedArgs,
            Class<T> configClass,
            List<String> requiredArgs,
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

        if (requiredArgs.stream().anyMatch(CliUtils::isInvalidPath)) {
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
