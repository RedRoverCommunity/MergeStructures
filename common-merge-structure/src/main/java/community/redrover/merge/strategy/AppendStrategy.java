package community.redrover.merge.strategy;

import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.util.FileUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

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
        try {
            String sourcePath = basePath.resolve(getConfig().getSourceFile()).toString();
            String targetPath = basePath.resolve(getConfig().getTargetFile()).toString();
            String resultPath = basePath.resolve(getConfig().getResultFile()).toString();

            Map<String, Object> sourceMap = FileUtils.loadFileToMap(sourcePath);
            Map<String, Object> targetMap = FileUtils.loadFileToMap(targetPath);

            Map<String, Object> mergedResult = new LinkedHashMap<>(sourceMap);
            mergedResult.putAll(targetMap);

            FileUtils.writeMapToFile(resultPath, mergedResult);
        } catch (IOException e) {
            throw new RuntimeException("Failed to execute append strategy", e);
        }
    }
}