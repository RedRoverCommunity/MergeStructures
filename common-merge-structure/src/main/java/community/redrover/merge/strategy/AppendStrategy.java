package community.redrover.merge.strategy;

import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.util.FileUtils;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.stream.Collectors;

public class AppendStrategy extends AbstractStrategy<AppendStrategyConfig> {

    private final Path basePath;

    public AppendStrategy(AppendStrategyConfig config) {
        this(config, Path.of("."));
    }

    public AppendStrategy(AppendStrategyConfig config, Path basePath) {
        super(config);
        this.basePath = basePath;
    }

    @Override
    public void execute() {
        LinkedHashMap<String, Object> sourceMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getSourceFile()));
        LinkedHashMap<String, Object> destinationMap = FileUtils.loadFileToMap(basePath.resolve(getConfig().getDestinationFile()));

        Set<String> commonKeys = sourceMap.keySet().stream()
                .filter(destinationMap::containsKey)
                .collect(Collectors.toSet());

        if (!commonKeys.isEmpty()) {
            throw new IllegalStateException("AppendStrategy error: Matching keys found: " + commonKeys);
        }

        LinkedHashMap<String, Object> mergedResult = new LinkedHashMap<>(destinationMap);
        mergedResult.putAll(sourceMap);

        FileUtils.writeMapToFile(basePath.resolve(getConfig().getResultFile()), mergedResult);
    }
}