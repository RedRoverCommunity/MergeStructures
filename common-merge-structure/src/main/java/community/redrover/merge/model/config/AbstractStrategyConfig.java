package community.redrover.merge.model.config;

import community.redrover.merge.model.Strategy;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class AbstractStrategyConfig {

    private String inputFile;
    private Strategy strategy;
}
