package community.redrover.merge.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
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
    }

    @ParameterizedTest
    @ValueSource(strings = {"yaml", "json"})
    public void testAppendStrategy_PositiveAndNegativeCases(String format) throws IOException {
        Path basePath = Paths.get("src/test/resources/append", format);

        ObjectMapper mapper = new ObjectMapper();
        AppendStrategyTestConfig config = mapper.readValue(basePath.resolve("config.json").toFile(),
                AppendStrategyTestConfig.class);

        String source = basePath.resolve(config.getSourceFile()).toString();
        String target = basePath.resolve(config.getTargetFile()).toString();
        String actualResult = basePath.resolve(config.getActualResultFile()).toString();
        String expectedResult = basePath.resolve(config.getExpectedResultFile()).toString();
        String errorTarget = basePath.resolve(config.getErrorTargetFile()).toString();

        Map<String, Object> sourceMap = FileUtils.loadFileToMap(source);
        Map<String, Object> targetMap = FileUtils.loadFileToMap(target);

        Map<String, Object> mergedResult = new LinkedHashMap<>(sourceMap);
        mergedResult.putAll(targetMap);
        FileUtils.writeMapToFile(actualResult, mergedResult);

        Map<String, Object> actualMap = FileUtils.loadFileToMap(actualResult);
        Map<String, Object> expectedMap = FileUtils.loadFileToMap(expectedResult);

        assertEquals(expectedMap, actualMap);

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