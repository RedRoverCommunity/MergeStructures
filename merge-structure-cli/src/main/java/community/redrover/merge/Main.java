package community.redrover.merge;

import community.redrover.merge.cli.CliException;
import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.model.Strategy;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                throw new CliException("No arguments provided.", true);
            }

            String strategyName = args[0].toLowerCase();
            String[] inputArgs  = Arrays.copyOfRange(args, 1, args.length);
            Strategy strategy;

            try {
                strategy = Strategy.valueOf(strategyName.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new CliException("Unknown strategy: " + strategyName, true);
            }

            CliUtils.executeStrategy(
                    strategyName,
                    inputArgs,
                    strategy.getConfigClass(),
                    strategyArgs -> strategy.buildConfig(
                            strategyArgs.source,
                            strategyArgs.destination,
                            strategyArgs.result
                    ),
                    config -> strategy.createStrategy(config).execute()
            );

        } catch (CliException e) {
            CliUtils.exitWithError(e);
        }
    }
}