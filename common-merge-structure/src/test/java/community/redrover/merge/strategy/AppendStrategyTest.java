package community.redrover.merge.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import community.redrover.merge.model.config.AppendStrategyConfig;
import community.redrover.merge.util.FileUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AppendStrategyTest {

    @Getter
    @NoArgsConstructor
    static class AppendStrategyTestConfig extends AppendStrategyConfig {

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
    public void testAppendStrategy_PositiveAndNegativeCases(String format) throws IOException {
        Path basePath = Paths.get("src/test/resources/append", format);

        String configFileName;
        ObjectMapper mapper;

        if ("json".equals(format)) {
            configFileName = "config.json";
            mapper = new ObjectMapper();
        } else {
            configFileName = "config.yaml";
            mapper = new ObjectMapper(new YAMLFactory());
        }

        AppendStrategyTestConfig config = mapper.readValue(
                basePath.resolve(configFileName).toFile(),
                AppendStrategyTestConfig.class
        );

        AppendStrategy strategy = new AppendStrategy(config, basePath);
        strategy.execute();

        assertEquals(FileUtils.loadFileToMap(basePath.resolve(config.getExpectedResultFile())),
                FileUtils.loadFileToMap(basePath.resolve(config.getActualResultFile())));

        assertThrows(IllegalStateException.class, () -> {
            Map<String, Object> intersection = new LinkedHashMap<>(FileUtils.loadFileToMap(basePath.resolve(config.getSourceFile())));
            intersection.keySet().retainAll(FileUtils.loadFileToMap(basePath.resolve(config.getErrorTargetFile())).keySet());

            if (!intersection.isEmpty()) {
                throw new IllegalStateException("AppendStrategy error: Matching keys found: " + intersection.keySet());
            }
        });
    }
}