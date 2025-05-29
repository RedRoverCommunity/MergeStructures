package community.redrover.merge.strategy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import community.redrover.merge.util.FileUtils;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


public class AppendStrategyTest {

    static class StrategyConfig {
        public String sourceFile;
        public String targetFile;
        public String actualResultFile;
        public String expectedResultFile;
        public String errorTargetFile;
    }

    @ParameterizedTest
    @ValueSource(strings = {"yaml", "json"})
    public void testAppendStrategy_PositiveAndNegativeCases(String format) throws IOException {
        String basePath = "src/test/resources/append/" + format + "/";
        String configPath = basePath + "config.json";

        ObjectMapper mapper = new ObjectMapper();
        List<StrategyConfig> configs = mapper.readValue(new File(configPath), new TypeReference<>() {});
        StrategyConfig config = configs.get(0);

        String source = Paths.get(basePath, config.sourceFile).toString();
        String target = Paths.get(basePath, config.targetFile).toString();
        String result = Paths.get(basePath, config.actualResultFile).toString();
        String expected = Paths.get(basePath, config.expectedResultFile).toString();
        String errorTarget = Paths.get(basePath, config.errorTargetFile).toString();

        Map<String, Object> sourceMap = FileUtils.loadFileToMap(source);
        Map<String, Object> targetMap = FileUtils.loadFileToMap(target);

        Map<String, Object> mergedResult = new LinkedHashMap<>(sourceMap);
        mergedResult.putAll(targetMap);
        FileUtils.writeMapToFile(result, mergedResult);

        Map<String, Object> actualMap = FileUtils.loadFileToMap(result);
        Map<String, Object> expectedMap = FileUtils.loadFileToMap(expected);

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