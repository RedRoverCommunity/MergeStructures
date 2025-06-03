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
        LinkedHashMap<String, Object> mergedResult = new LinkedHashMap<>(FileUtils.loadFileToMap(basePath.resolve(getConfig().getSourceFile())));

        mergedResult.putAll(FileUtils.loadFileToMap(basePath.resolve(getConfig().getTargetFile())));

        FileUtils.writeMapToFile(basePath.resolve(getConfig().getResultFile()), mergedResult);
    }
}