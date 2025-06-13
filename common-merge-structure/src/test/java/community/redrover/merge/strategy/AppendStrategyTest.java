package community.redrover.merge.strategy;

import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.util.FileUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppendStrategyTest {

    @Getter
    @NoArgsConstructor
    @SuppressWarnings("unused")
    static class AppendStrategyTestConfig extends AppendStrategyConfig {

        private String actualResultFile;
        private String expectedResultFile;
        private String errorDestinationFile;

        @Override
        public String getResultFile() {
            return actualResultFile;
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"yaml", "json"})
    void testAppendStrategyPositiveAndNegativeCases(String format) {
        Path basePath = Paths.get("src/test/resources/append", format);
        Path configPath = basePath.resolve("config." + format);

        AppendStrategyTestConfig config = FileUtils.loadFileToObject(configPath, AppendStrategyTestConfig.class);

        AppendStrategy strategy = new AppendStrategy(config, basePath);
        strategy.execute();

        assertEquals(
                FileUtils.loadFileToMap(basePath.resolve(config.getExpectedResultFile())),
                FileUtils.loadFileToMap(basePath.resolve(config.getActualResultFile()))
        );

        config.setDestinationFile(config.getErrorDestinationFile());
        AppendStrategy strategyWithConflict = new AppendStrategy(config, basePath);

        assertThrows(IllegalStateException.class, strategyWithConflict::execute);
    }
}