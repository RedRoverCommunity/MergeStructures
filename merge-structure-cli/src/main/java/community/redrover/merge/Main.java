package community.redrover.merge;

import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.cli.command.AppendCommand;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            CliUtils.printUsageAndExit();
        }

        String strategyName = args[0].toLowerCase();
        String[] strategyArgs = java.util.Arrays.copyOfRange(args, 1, args.length);

        try {
            switch (strategyName) {
                case "append" -> AppendCommand.run(strategyArgs);
                default -> {
                    System.err.println("Unknown strategy: " + strategyName);
                    CliUtils.printUsageAndExit();
                }
            }
        } catch (Exception e) {
            System.err.println("Error executing strategy: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}