package community.redrover.merge;

import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.strategy.AppendStrategy;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0 || Arrays.asList(args).contains("--help")) {
            printUsageAndExit();
        }

        String strategyName = args[0].toLowerCase();
        String[] strategyArgs = Arrays.copyOfRange(args, 1, args.length);

        try {
            switch (strategyName) {
                case "append":
                    runAppend(strategyArgs);
                    break;
                default:
                    System.err.println("Unknown strategy: " + strategyName);
                    printUsageAndExit();
            }
        } catch (Exception e) {
            System.err.println("Error executing strategy: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void runAppend(String[] args) {
        Map<String, String> argMap = parseArgs(args);
        AppendStrategyConfig config;

        if (argMap.containsKey("--config")) {
            String configPath = argMap.get("--config");
            if (configPath == null || configPath.startsWith("--")) {
                System.err.println("Invalid value for --config");
                printUsageAndExit();
            }
            config = FileUtils.loadFileToObject(Paths.get(configPath), AppendStrategyConfig.class);
        } else {
            validateRequiredParams(argMap, "--source", "--destination", "--result");
            config = new AppendStrategyConfig(Strategy.Append, argMap.get("--source"), argMap.get("--destination"), argMap.get("--result"));
        }

        AppendStrategy strategy = new AppendStrategy(config);
        strategy.execute();
        System.out.println("Append strategy completed successfully.");
    }

    private static Map<String, String> parseArgs(String[] args) {
        Map<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (!args[i].startsWith("--")) {
                System.err.println("Unexpected argument: " + args[i]);
                printUsageAndExit();
            }
            if (i + 1 >= args.length || args[i + 1].startsWith("--")) {
                System.err.println("Missing value for parameter: " + args[i]);
                printUsageAndExit();
            }
            map.put(args[i], args[i + 1]);
            i++;
        }

        return map;
    }

    private static void validateRequiredParams(Map<String, String> argMap, String... requiredKeys) {
        List<String> missing = new ArrayList<>();

        for (String key : requiredKeys) {
            String value = argMap.get(key);
            if (value == null || value.startsWith("--")) {
                missing.add(key);
            }
        }

        if (!missing.isEmpty()) {
            System.err.println("Missing or invalid value for parameter(s): " + String.join(", ", missing));
            printUsageAndExit();
        }
    }

    private static void printUsageAndExit() {
        System.out.println("""
                
                Usage:
                  java -jar merge-structure-cli.jar <strategy> --config <path>
                  OR
                  java -jar merge-structure-cli.jar <strategy> --source <path> --destination <path> --result <path>
                
                Supported strategies: append, merge, replace, extend
                Supported formats: JSON (.json), YAML (.yaml or .yml)
                
                Use '--help' to see this message again.
                """);
        System.exit(1);
    }
}