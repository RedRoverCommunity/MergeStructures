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
        LinkedHashMap<String, Object> targetMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getTargetFile()));

        LinkedHashMap<String, Object> mergedResult = new LinkedHashMap<>();

        for (String key : targetMap.keySet()) {

            Object sourceValue = sourceMap.get(key);
            Object targetValue = targetMap.get(key);

            if (targetValue == null) {
                throw new IllegalStateException("MergeStrategy error: Null value encountered for key: " + key);
            }
            if (sourceValue == null) {
                mergedResult.put(key, targetValue);
                continue;
            }
            if (sourceValue.equals(targetValue)) {
                throw new IllegalStateException("MergeStrategy error: Duplicate root key with same content: " + key);
            }

            LinkedHashMap<String, Object> mergedInner = new LinkedHashMap<>((LinkedHashMap<String, Object>) sourceValue);
            mergedInner.putAll((LinkedHashMap<String, Object>) targetValue);
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