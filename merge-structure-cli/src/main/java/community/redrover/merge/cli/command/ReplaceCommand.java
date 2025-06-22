package community.redrover.merge.cli.command;

import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.cli.ParsedStrategy;
import community.redrover.merge.cli.StrategyArgs;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.ReplaceStrategyConfig;
import community.redrover.merge.strategy.ReplaceStrategy;
import java.util.List;

public final class ReplaceCommand {

    private ReplaceCommand() {}

    public static void run(String[] argv) {
        ParsedStrategy parsed = CliUtils.parseArgs("replace", argv);
        StrategyArgs args     = parsed.args();

        ReplaceStrategyConfig cfg = CliUtils.loadConfigOrUseArgs(
                parsed,
                ReplaceStrategyConfig.class,
                List.of(args.source, args.destination, args.result),
                () -> new ReplaceStrategyConfig(
                        Strategy.REPLACE,
                        args.source,
                        args.destination,
                        args.result
                )
        );

        new ReplaceStrategy(cfg).execute();
        System.out.println("Replace strategy completed successfully.");
    }
}