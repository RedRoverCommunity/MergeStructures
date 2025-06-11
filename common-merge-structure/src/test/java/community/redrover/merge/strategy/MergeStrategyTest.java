package community.redrover.merge.strategy;

import community.redrover.merge.model.config.MergeStrategyConfig;
import community.redrover.merge.util.FileUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MergeStrategyTest {

    @Getter
    @NoArgsConstructor
    @SuppressWarnings("unused")
    static class MergeStrategyTestConfig extends MergeStrategyConfig {

        private String actualResultFile;
        private String expectedResultFile;
        private String errorTargetFile;

        @Override
        public String getResultFile() {
            return actualResultFile;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"yaml", "json"})
    void testMergeStrategyPositiveAndNegativeCases(String format) {
        Path basePath = Paths.get("src/test/resources/merge", format);
        Path configPath = basePath.resolve("config." + format);

        MergeStrategyTestConfig config = FileUtils.loadFileToObject(configPath, MergeStrategyTestConfig.class);
        MergeStrategy strategy = new MergeStrategy(config, basePath);
        strategy.execute();

        assertEquals(
                FileUtils.loadFileToMap(basePath.resolve(config.getExpectedResultFile())),
                FileUtils.loadFileToMap(basePath.resolve(config.getActualResultFile()))
        );

        config.setTargetFile(config.getErrorTargetFile());
        MergeStrategy strategyWithConflict = new MergeStrategy(config, basePath);

        assertThrows(IllegalStateException.class, strategyWithConflict::execute);
    }
}