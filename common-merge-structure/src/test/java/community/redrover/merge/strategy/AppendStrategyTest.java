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

        String actualResult = basePath.resolve(config.getActualResultFile()).toString();
        String expectedResult = basePath.resolve(config.getExpectedResultFile()).toString();

        Map<String, Object> actualMap = FileUtils.loadFileToMap(actualResult);
        Map<String, Object> expectedMap = FileUtils.loadFileToMap(expectedResult);

        assertEquals(expectedMap, actualMap);

        String source = basePath.resolve(config.getSourceFile()).toString();
        String errorTarget = basePath.resolve(config.getErrorTargetFile()).toString();

        Map<String, Object> sourceMap = FileUtils.loadFileToMap(source);
        Map<String, Object> errorTargetMap = FileUtils.loadFileToMap(errorTarget);

        assertThrows(IllegalStateException.class, () -> {
            Map<String, Object> intersection = new LinkedHashMap<>(sourceMap);
            intersection.keySet().retainAll(errorTargetMap.keySet());

            if (!intersection.isEmpty()) {
                throw new IllegalStateException("AppendStrategy error: Matching keys found: " + intersection.keySet());
            }
        });
    }
}