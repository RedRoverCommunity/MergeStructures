package community.redrover.merge.strategy;

import community.redrover.merge.model.config.ExtendStrategyConfig;
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

public class ExtendStrategyTest {

    @Getter
    @NoArgsConstructor
    @SuppressWarnings("unused")
    static class ExtendStrategyTestConfig extends ExtendStrategyConfig {

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
    void testExtendStrategyPositiveAndNegativeCases(SupportedExtension ext) {
        String format = ext.getValue();
        Path basePath = Paths.get("src/test/resources/extend", format);
        Path configPath = basePath.resolve("config." + format);

        ExtendStrategyTestConfig config =
                FileUtils.loadFileToObject(configPath, ExtendStrategyTestConfig.class);

        ExtendStrategy strategy = new ExtendStrategy(config, basePath);
        strategy.execute();

        assertEquals(
                FileUtils.loadFileToMap(basePath.resolve(config.getExpectedResultFile())),
                FileUtils.loadFileToMap(basePath.resolve(config.getActualResultFile()))
        );

        config.setDestinationFile(config.getErrorDestinationFile());
        ExtendStrategy strategyWithConflict = new ExtendStrategy(config, basePath);
        assertThrows(IllegalStateException.class, strategyWithConflict::execute);
    }
}