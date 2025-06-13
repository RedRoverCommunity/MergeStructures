package community.redrover.merge.model.config;

import community.redrover.merge.model.Strategy;

public class ReplaceStrategyConfig extends AbstractStrategyConfig {

    @SuppressWarnings("unused")
    public ReplaceStrategyConfig() {
        super();
    }

    @SuppressWarnings("unused")
    public ReplaceStrategyConfig(Strategy strategy, String sourceFile, String destinationFile, String resultFile) {
        super(strategy, sourceFile, destinationFile, resultFile);
    }
}
