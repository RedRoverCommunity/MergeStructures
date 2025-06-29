package community.redrover.merge.model;

import community.redrover.merge.model.config.*;
import community.redrover.merge.strategy.*;
import java.util.Arrays;
import java.util.Optional;

public enum Strategy {

    APPEND {
        @Override
        public AbstractStrategyConfig buildConfig(String source, String destination, String result) {
            return new AppendStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<? extends AbstractStrategyConfig> getConfigClass() {
            return AppendStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<?> createStrategy(AbstractStrategyConfig config) {
            return new AppendStrategy((AppendStrategyConfig) config);
        }
    },

    EXTEND {
        @Override
        public AbstractStrategyConfig buildConfig(String source, String destination, String result) {
            return new ExtendStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<? extends AbstractStrategyConfig> getConfigClass() {
            return ExtendStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<?> createStrategy(AbstractStrategyConfig config) {
            return new ExtendStrategy((ExtendStrategyConfig) config);
        }
    },

    MERGE {
        @Override
        public AbstractStrategyConfig buildConfig(String source, String destination, String result) {
            return new MergeStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<? extends AbstractStrategyConfig> getConfigClass() {
            return MergeStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<?> createStrategy(AbstractStrategyConfig config) {
            return new MergeStrategy((MergeStrategyConfig) config);
        }
    },

    REPLACE {
        @Override
        public AbstractStrategyConfig buildConfig(String source, String destination, String result) {
            return new ReplaceStrategyConfig(this, source, destination, result);
        }

        @Override
        public Class<? extends AbstractStrategyConfig> getConfigClass() {
            return ReplaceStrategyConfig.class;
        }

        @Override
        public AbstractStrategy<?> createStrategy(AbstractStrategyConfig config) {
            return new ReplaceStrategy((ReplaceStrategyConfig) config);
        }
    };

    public abstract AbstractStrategyConfig buildConfig(String source, String destination, String result);

    public abstract Class<? extends AbstractStrategyConfig> getConfigClass();

    public abstract AbstractStrategy<?> createStrategy(AbstractStrategyConfig config);

    public void execute(AbstractStrategyConfig config) {
        createStrategy(config).execute();
    }

    public static Optional<Strategy> fromName(String name) {
        return Arrays.stream(values())
                .filter(strategy -> strategy.name().equalsIgnoreCase(name))
                .findFirst();
    }

    public String getName() {
        return name().substring(0, 1).toUpperCase() + name().substring(1).toLowerCase();
    }
}