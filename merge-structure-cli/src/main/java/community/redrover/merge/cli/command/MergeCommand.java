package community.redrover.merge.cli.command;

import community.redrover.merge.cli.CliUtils;
import community.redrover.merge.cli.ParsedStrategy;
import community.redrover.merge.cli.StrategyArgs;
import community.redrover.merge.model.Strategy;
import community.redrover.merge.model.config.MergeStrategyConfig;
import community.redrover.merge.strategy.MergeStrategy;
import java.util.List;

public final class MergeCommand {

    private MergeCommand() {}

    public static void run(String[] argv) {
        ParsedStrategy parsed = CliUtils.parseArgs("merge", argv);
        StrategyArgs args     = parsed.args();

        MergeStrategyConfig cfg = CliUtils.loadConfigOrUseArgs(
                parsed,
                MergeStrategyConfig.class,
                List.of(args.source, args.destination, args.result),
                () -> new MergeStrategyConfig(
                        Strategy.MERGE,
                        args.source,
                        args.destination,
                        args.result
                )
        );

        new MergeStrategy(cfg).execute();
        System.out.println("Merge strategy completed successfully.");
    }
}