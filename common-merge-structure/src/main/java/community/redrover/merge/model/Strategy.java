package community.redrover.merge.model;

import community.redrover.merge.model.config.*;
import community.redrover.merge.strategy.*;

public enum Strategy {

    APPEND() {
        @Override
        public AbstractStrategy<AppendStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new AppendStrategy((AppendStrategyConfig) strategyConfig);
        }
    },

    EXTEND() {
        @Override
        public AbstractStrategy<ExtendStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new ExtendStrategy((ExtendStrategyConfig) strategyConfig);
        }
    },

    MERGE() {
        @Override
        protected AbstractStrategy<MergeStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new MergeStrategy((MergeStrategyConfig) strategyConfig);
        }
    },

    REPLACE() {
        @Override
        protected AbstractStrategy<ReplaceStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new ReplaceStrategy((ReplaceStrategyConfig) strategyConfig);
        }
    };

    protected abstract AbstractStrategy<?> createStrategy(AbstractStrategyConfig strategyConfig);
}
