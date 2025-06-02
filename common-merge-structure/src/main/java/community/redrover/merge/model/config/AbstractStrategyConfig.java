package community.redrover.merge.model.config;

import community.redrover.merge.model.Strategy;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public abstract class AbstractStrategyConfig {

    private Strategy strategy;
    private String sourceFile;
    private String targetFile;
    private String resultFile;
}
