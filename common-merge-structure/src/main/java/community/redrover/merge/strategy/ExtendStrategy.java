package community.redrover.merge.strategy;

import community.redrover.merge.model.config.ExtendStrategyConfig;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Set;

public class ExtendStrategy extends AbstractStrategy<ExtendStrategyConfig> {

    private final Path basePath;

    public ExtendStrategy(ExtendStrategyConfig config) {
        this(config, Path.of("."));
    }

    public ExtendStrategy(ExtendStrategyConfig config, Path basePath) {
        super(config);
        this.basePath = basePath;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void execute() {
        LinkedHashMap<String, Object> sourceMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getSourceFile()));
        LinkedHashMap<String, Object> targetMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getTargetFile()));

        Set<String> sourceKeys = sourceMap.keySet();
        Set<String> targetKeys = targetMap.keySet();

        if (!sourceKeys.equals(targetKeys)) {
            throw new IllegalStateException(
                    "ExtendStrategy error: Root keys do not match exactly: source = " + sourceKeys + ", target = " + targetKeys
            );
        }

        LinkedHashMap<String, Object> mergedResult = new LinkedHashMap<>();

        for (String key : sourceKeys) {
            if (sourceMap.get(key) == null || targetMap.get(key) == null) {
                throw new IllegalStateException("ExtendStrategy error: Null value encountered for key: " + key);
            }
            LinkedHashMap<String, Object> mergedInner = new LinkedHashMap<>((LinkedHashMap<String, Object>) sourceMap.get(key));
            mergedInner.putAll((LinkedHashMap<String, Object>) targetMap.get(key));
            mergedResult.put(key, mergedInner);
        }

        FileUtils.writeMapToFile(basePath.resolve(getConfig().getResultFile()), mergedResult);
    }
}