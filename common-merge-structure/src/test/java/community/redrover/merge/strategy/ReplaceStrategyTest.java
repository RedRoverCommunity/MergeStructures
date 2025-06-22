package community.redrover.merge.strategy;

import community.redrover.merge.model.config.ReplaceStrategyConfig;
import community.redrover.merge.util.FileUtils;
import community.redrover.merge.util.SupportedExtension;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ReplaceStrategyTest {

    @Getter
    @NoArgsConstructor
    @SuppressWarnings("unused")
    static class ReplaceStrategyTestConfig extends ReplaceStrategyConfig {
        private String actualResultFile;
        private String expectedResultFile;
        private String errorDestinationFile;

        @Override
        public String getResultFile() {
            return actualResultFile;
        }
    }

    @ParameterizedTest
    @EnumSource(SupportedExtension.class)
    void testReplaceStrategyPositiveAndNegativeCases(SupportedExtension ext) {
        String format = ext.getValue();
        Path basePath   = Paths.get("src/test/resources/replace", format);
        Path configPath = basePath.resolve("config." + format);

        ReplaceStrategyTestConfig config =
                FileUtils.loadFileToObject(configPath, ReplaceStrategyTestConfig.class);

        ReplaceStrategy strategy = new ReplaceStrategy(config, basePath);
        strategy.execute();

        assertEquals(
                FileUtils.loadFileToMap(basePath.resolve(config.getExpectedResultFile())),
                FileUtils.loadFileToMap(basePath.resolve(config.getActualResultFile()))
        );

        config.setDestinationFile(config.getErrorDestinationFile());
        ReplaceStrategy strategyWithConflict = new ReplaceStrategy(config, basePath);
        assertThrows(IllegalStateException.class, strategyWithConflict::execute);
    }
}
