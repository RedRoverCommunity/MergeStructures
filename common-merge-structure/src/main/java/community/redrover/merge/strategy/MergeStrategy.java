package community.redrover.merge.strategy;

import community.redrover.merge.model.config.MergeStrategyConfig;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Path;
import java.util.LinkedHashMap;

public class MergeStrategy extends AbstractStrategy<MergeStrategyConfig> {

    private final Path basePath;

    public MergeStrategy(MergeStrategyConfig config) {
        this(config, Path.of("."));
    }

    public MergeStrategy(MergeStrategyConfig config, Path basePath) {
        super(config);
        this.basePath = basePath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() {
        LinkedHashMap<String, Object> sourceMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getSourceFile()));
        LinkedHashMap<String, Object> destinationMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getDestinationFile()));

        LinkedHashMap<String, Object> mergedResult = new LinkedHashMap<>();

        for (String key : destinationMap.keySet()) {

            Object sourceValue = sourceMap.get(key);
            Object destinationValue = destinationMap.get(key);

            if (destinationValue == null) {
                throw new IllegalStateException("MergeStrategy error: Null value encountered for key: " + key);
            }
            if (sourceValue == null) {
                mergedResult.put(key, destinationValue);
                continue;
            }
            if (sourceValue.equals(destinationValue)) {
                throw new IllegalStateException("MergeStrategy error: Duplicate root key with same content: " + key);
            }

            LinkedHashMap<String, Object> mergedInner = new LinkedHashMap<>((LinkedHashMap<String, Object>) destinationValue);
            mergedInner.putAll((LinkedHashMap<String, Object>) sourceValue);
            mergedResult.put(key, mergedInner);
        }

        for (String key : sourceMap.keySet()) {
            if (!mergedResult.containsKey(key)) {
                mergedResult.put(key, sourceMap.get(key));
            }
        }

        FileUtils.writeMapToFile(basePath.resolve(getConfig().getResultFile()), mergedResult);
    }
}