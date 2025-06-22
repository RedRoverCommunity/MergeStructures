package community.redrover.merge.cli.command;

import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.cli.ParsedStrategy;
import community.redrover.merge.cli.StrategyArgs;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.strategy.AppendStrategy;
import java.util.List;

public final class AppendCommand {

    private AppendCommand() {}

    public static void run(String[] args) {
        ParsedStrategy parsedArgs = CliUtils.parseArgs("append", args);
        StrategyArgs strategyArgs = parsedArgs.args();

        AppendStrategyConfig config = CliUtils.loadConfigOrUseArgs(
                parsedArgs,
                AppendStrategyConfig.class,
                List.of(strategyArgs.source, strategyArgs.destination, strategyArgs.result),
                () -> new AppendStrategyConfig(
                        Strategy.APPEND,
                        strategyArgs.source,
                        strategyArgs.destination,
                        strategyArgs.result
                )
        );

        new AppendStrategy(config).execute();
        System.out.println("Append strategy completed successfully.");
    }
}
