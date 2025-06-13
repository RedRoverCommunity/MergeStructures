package community.redrover.merge.strategy;

import community.redrover.merge.model.config.ReplaceStrategyConfig;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class ReplaceStrategy extends AbstractStrategy<ReplaceStrategyConfig> {

    private final Path basePath;

    public ReplaceStrategy(ReplaceStrategyConfig config) {
        this(config, Path.of("."));
    }

    public ReplaceStrategy(ReplaceStrategyConfig config, Path basePath) {
        super(config);
        this.basePath = basePath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() {
        LinkedHashMap<String, Object> sourceMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getSourceFile()));
        LinkedHashMap<String, Object> destinationMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getDestinationFile()));

        LinkedHashMap<String, Object> replacedResult = new LinkedHashMap<>();

        for (String key : destinationMap.keySet()) {
            if (!sourceMap.containsKey(key)) {
                throw new IllegalStateException("ReplaceStrategy error: Key not found in source: " + key);
            }

            LinkedHashMap<String, Object> sourceValue = (LinkedHashMap<String, Object>) sourceMap.get(key);
            LinkedHashMap<String, Object> destinationValue = (LinkedHashMap<String, Object>) destinationMap.get(key);

            if (!sourceValue.keySet().equals(destinationValue.keySet())) {
                throw new IllegalStateException("ReplaceStrategy error: Mismatched inner keys for root key: " + key);
            }

            replacedResult.put(key, sourceValue);
        }

        FileUtils.writeMapToFile(basePath.resolve(getConfig().getResultFile()), replacedResult);
    }
}
