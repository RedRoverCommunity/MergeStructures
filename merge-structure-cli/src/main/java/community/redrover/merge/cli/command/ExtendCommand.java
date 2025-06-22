package community.redrover.merge.cli.command;

import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.cli.ParsedStrategy;
import community.redrover.merge.cli.StrategyArgs;
import community.redrover.merge.model.Strategy;                    // <— correct import
import community.redrover.merge.model.config.ExtendStrategyConfig;
import community.redrover.merge.strategy.ExtendStrategy;
import java.util.List;

public final class ExtendCommand {

    private ExtendCommand() {}

    public static void run(String[] argv) {
        ParsedStrategy parsed = CliUtils.parseArgs("extend", argv);
        StrategyArgs args    = parsed.args();

        ExtendStrategyConfig cfg = CliUtils.loadConfigOrUseArgs(
                parsed,
                ExtendStrategyConfig.class,
                List.of(args.source, args.destination, args.result),
                () -> new ExtendStrategyConfig(
                        Strategy.EXTEND,          // now resolves correctly
                        args.source,
                        args.destination,
                        args.result
                )
        );

        new ExtendStrategy(cfg).execute();
        System.out.println("Extend strategy completed successfully.");
    }
}