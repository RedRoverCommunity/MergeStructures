package community.redrover.merge.strategy;

import community.redrover.merge.model.config.AbstractStrategyConfig;
import lombok.AccessLevel;
import lombok.Getter;

public abstract class AbstractStrategy<Config extends AbstractStrategyConfig> {

    @Getter(AccessLevel.PROTECTED)
    private Config config;

    public AbstractStrategy(Config config) {
        this.config = config;
    }

    public abstract void execute();
}
