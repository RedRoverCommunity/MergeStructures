package community.redrover.merge.model;

import community.redrover.merge.model.config.*;
import community.redrover.merge.strategy.*;

public enum Strategy {

    Append() {
        @Override
        public AbstractStrategy<AppendStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new AppendStrategy((AppendStrategyConfig) strategyConfig);
        }
    },

    Extend() {
        @Override
        public AbstractStrategy<ExtendStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new ExtendStrategy((ExtendStrategyConfig) strategyConfig);
        }
    },

    Merge() {
        @Override
        protected AbstractStrategy<MergeStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new MergeStrategy((MergeStrategyConfig) strategyConfig);
        }
    },

    Replace() {
        @Override
        protected AbstractStrategy<ReplaceStrategyConfig> createStrategy(AbstractStrategyConfig strategyConfig) {
            return new ReplaceStrategy((ReplaceStrategyConfig) strategyConfig);
        }
    };

    protected abstract AbstractStrategy<?> createStrategy(AbstractStrategyConfig strategyConfig);
}
