package community.redrover.merge.model.config;

import community.redrover.merge.model.Strategy;

public class ExtendStrategyConfig extends AbstractStrategyConfig {

    public ExtendStrategyConfig() {
        super();
    }

    public ExtendStrategyConfig(Strategy strategy, String sourceFile, String targetFile, String resultFile) {
        super(strategy, sourceFile, targetFile, resultFile);
    }
}