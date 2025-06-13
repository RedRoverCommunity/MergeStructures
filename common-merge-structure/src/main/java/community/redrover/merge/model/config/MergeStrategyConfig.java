package community.redrover.merge.model.config;

import community.redrover.merge.model.Strategy;

public class MergeStrategyConfig extends AbstractStrategyConfig {

    @SuppressWarnings("unused")
    public MergeStrategyConfig() {
        super();
    }

    @SuppressWarnings("unused")
    public MergeStrategyConfig(Strategy strategy, String sourceFile, String destinationFile, String resultFile) {
        super(strategy, sourceFile, destinationFile, resultFile);
    }
}