package community.redrover.merge.cli;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Paths;
import java.util.List;
import java.util.function.Supplier;

public class CliUtils {

    public static ParsedStrategy parseArgs(String strategyName, String[] args) {
        StrategyArgs strategyArgs = new StrategyArgs();
        JCommander jc = JCommander.newBuilder()
                .addObject(strategyArgs)
                .programName("merge-structure-cli " + strategyName)
                .build();

        try {
            jc.parse(args);
            if (strategyArgs.help) {
                jc.usage();
                System.exit(0);
            }
        } catch (ParameterException e) {
            System.err.println("Error: " + e.getMessage());
            jc.usage();
            System.exit(1);
        }

        return new ParsedStrategy(strategyArgs, jc);
    }

    public static <T> T loadConfigOrUseArgs(
            ParsedStrategy parsed,
            Class<T> configClass,
            List<String> requiredArgs,
            Supplier<T> fallbackConfigSupplier
    ) {
        StrategyArgs args = parsed.args();
        JCommander jc = parsed.commander();

        if (args.config != null) {
            if (isInvalidPath(args.config)) {
                System.err.println("Invalid value for --config: " + args.config);
                jc.usage();
                System.exit(1);
            }

            return FileUtils.loadFileToObject(Paths.get(args.config), configClass);
        }

        if (requiredArgs.stream().anyMatch(CliUtils::isInvalidPath)) {
            System.err.println("Missing or invalid required arguments.");
            jc.usage();
            System.exit(1);
        }

        return fallbackConfigSupplier.get();
    }

    public static boolean isInvalidPath(String path) {
        return path == null || path.isBlank() || path.startsWith("--");
    }

    public static void printUsageAndExit() {
        System.out.println("""
                Usage:
                  java -jar merge-structure-cli.jar <strategy> [options]

                Example:
                  java -jar merge-structure-cli.jar append --config config.yaml
                  OR
                  java -jar merge-structure-cli.jar append --source source.yaml --destination dest.yaml --result output.yaml

                Supported strategies: append, merge, replace, extend
                Supported formats: JSON (.json), YAML (.yaml or .yml)

                Use '--help' to see this message again.
                """);
        System.exit(1);
    }
}
