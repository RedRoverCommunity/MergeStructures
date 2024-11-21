package community.redrover.merge.model;

import community.redrover.merge.model.config.AbstractStrategyConfig;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class GeneralConfig {

    private List<AbstractStrategyConfig> inputs;
    private String output;
}
