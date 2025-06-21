package community.redrover.merge;

import community.redrover.merge.cli.CliException;
import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.cli.command.AppendCommand;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            throw new CliException("No arguments provided.", true);
        }

        String strategyName = args[0].toLowerCase();
        String[] strategyArgs = java.util.Arrays.copyOfRange(args, 1, args.length);

        try {
            switch (strategyName) {
                case "append" -> AppendCommand.run(strategyArgs);
                default -> throw new CliException("Unknown strategy: " + strategyName, true);
            }
        } catch (CliException e) {
            if (e.getMessage() != null) {
                System.err.println(e.getMessage());
            }
            if (e.shouldShowUsage()) {
                if (e.getCommander() != null) {
                    e.getCommander().usage();
                } else {
                    CliUtils.printUsage();
                }
            }
            System.exit(1);
        }
    }
}