package community.redrover.merge.strategy;

import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.util.FileUtils;
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
        Path sourcePath = basePath.resolve(getConfig().getSourceFile());
        Path targetPath = basePath.resolve(getConfig().getTargetFile());
        Path resultPath = basePath.resolve(getConfig().getResultFile());

        Map<String, Object> mergedResult = new LinkedHashMap<>(FileUtils.loadFileToMap(sourcePath));
        mergedResult.putAll(FileUtils.loadFileToMap(targetPath));

        FileUtils.writeMapToFile(resultPath, mergedResult);
    }
}