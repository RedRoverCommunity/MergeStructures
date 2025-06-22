package community.redrover.merge.model;

import community.redrover.merge.model.config.*;
import community.redrover.merge.strategy.*;

public enum Strategy {

    APPEND {
        @Override
        public AppendStrategyConfig buildConfig(String source, String destination, String result) {
            return new AppendStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<AppendStrategyConfig> getConfigClass() {
            return AppendStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<AppendStrategyConfig> createStrategy(AbstractStrategyConfig config) {
            return new AppendStrategy((AppendStrategyConfig) config);
        }
    },

    EXTEND {
        @Override
        public ExtendStrategyConfig buildConfig(String source, String destination, String result) {
            return new ExtendStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<ExtendStrategyConfig> getConfigClass() {
            return ExtendStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<ExtendStrategyConfig> createStrategy(AbstractStrategyConfig config) {
            return new ExtendStrategy((ExtendStrategyConfig) config);
        }
    },

    MERGE {
        @Override
        public MergeStrategyConfig buildConfig(String source, String destination, String result) {
            return new MergeStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<MergeStrategyConfig> getConfigClass() {
            return MergeStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<MergeStrategyConfig> createStrategy(AbstractStrategyConfig config) {
            return new MergeStrategy((MergeStrategyConfig) config);
        }
    },

    REPLACE {
        @Override
        public ReplaceStrategyConfig buildConfig(String source, String destination, String result) {
            return new ReplaceStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<ReplaceStrategyConfig> getConfigClass() {
            return ReplaceStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<ReplaceStrategyConfig> createStrategy(AbstractStrategyConfig config) {
            return new ReplaceStrategy((ReplaceStrategyConfig) config);
        }
    };

    public abstract AbstractStrategy<? extends AbstractStrategyConfig> createStrategy(AbstractStrategyConfig config);
    public abstract Class<? extends AbstractStrategyConfig> getConfigClass();
    public abstract AbstractStrategyConfig buildConfig(String source, String destination, String result);
}