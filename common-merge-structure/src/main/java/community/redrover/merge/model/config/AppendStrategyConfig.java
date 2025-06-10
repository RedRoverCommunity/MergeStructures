package community.redrover.merge.model.config;

import community.redrover.merge.model.Strategy;

public class AppendStrategyConfig extends AbstractStrategyConfig {

    @SuppressWarnings("unused")
    public AppendStrategyConfig() {
        super();
    }

    @SuppressWarnings("unused")
    public AppendStrategyConfig(Strategy strategy, String sourceFile, String targetFile, String resultFile) {
        super(strategy, sourceFile, targetFile, resultFile);
    }
}