package community.redrover.merge;

import community.redrover.merge.cli.CliException;
import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AbstractStrategyConfig;
import lombok.extern.slf4j.Slf4j;
import java.util.Arrays;

@Slf4j
public class Main {

    public static void main(String[] args) {
        try {
            CliUtils.validateArgs(args);
            Strategy strategy = CliUtils.resolveStrategy(args[0]);
            AbstractStrategyConfig config = CliUtils.buildStrategyConfig(Arrays.copyOfRange(args, 1, args.length), strategy);

            strategy.execute(config);

            CliUtils.printAndLogCliInfo(strategy.getName() + " strategy completed successfully.");
        } catch (CliException e) {
            CliUtils.exitWithError(e);
        }
    }
}