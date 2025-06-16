package community.redrover.merge.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import static org.junit.jupiter.api.Assertions.*;

public class FileUtilsTest {

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TestConfig {
        public String name;
        public int version;

        @Override
        public String toString() {
            return "TestConfig{name='" + name + "', version=" + version + "}";
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;

            TestConfig that = (TestConfig) o;
            return version == that.version && Objects.equals(name, that.name);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + version;
            return result;
        }
    }

    @Getter
    private static class TempFile implements AutoCloseable {
        private final Path path;

        TempFile(String prefix, String suffix) {
            try {
                this.path = Files.createTempFile(prefix, suffix);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to create temp file with prefix '%s' and suffix '%s'".formatted(prefix, suffix), e);
            }
        }

        public void write(String content) {
            try {
                Files.writeString(path, content);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to write to temp file: " + path, e);
            }
        }

        @Override
        public void close() {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                throw new UncheckedIOException("Failed to delete existing temp file: " + path, e);
            }
        }
    }

    @Test
    void testEmptyFileThrowsException() {
        try (TempFile emptyFile = new TempFile("empty_file", ".test")) {
            emptyFile.write("");
            assertTrue(assertThrows(UncheckedIOException.class,
                    () -> FileUtils.loadFileToMap(emptyFile.getPath())).getMessage().contains("File is empty"));
        }
    }

    @Test
    void testValidJsonFileReturnsExpectedMap() {
        try (TempFile tempJsonFile = new TempFile("test", ".json")) {
            tempJsonFile.write("""
                    {
                      "key1": "value1",
                      "key2": {
                        "nestedKey": "nestedValue"
                      }
                    }
                    """);
            var result = FileUtils.loadFileToMap(tempJsonFile.getPath());
            assertEquals("value1", result.get("key1"));
            assertEquals("nestedValue", ((Map<?, ?>) result.get("key2")).get("nestedKey"));
        }
    }

    @Test
    void testValidYamlFileReturnsExpectedMap() {
        try (TempFile tempYamlFile = new TempFile("test", ".yaml")) {
            tempYamlFile.write("""
                    key1: value1
                    key2:
                      nestedKey: nestedValue
                    """);
            var result = FileUtils.loadFileToMap(tempYamlFile.getPath());
            assertEquals("value1", result.get("key1"));
            assertEquals("nestedValue", ((Map<?, ?>) result.get("key2")).get("nestedKey"));
        }
    }

    @Test
    void testFileWithoutExtensionThrowsException() {
        try (TempFile noExtFile = new TempFile("test", "")) {
            noExtFile.write("key1: value1");
            assertTrue(assertThrows(IllegalArgumentException.class,
                    () -> FileUtils.loadFileToMap(noExtFile.getPath())).getMessage().contains("File does not have a valid extension"));
        }
    }

    @Test
    void testFileWithEndingDotThrowsException() {
        try (TempFile endDotFile = new TempFile("test", ".")) {
            endDotFile.write("key1: value1");
            assertTrue(assertThrows(IllegalArgumentException.class,
                    () -> FileUtils.loadFileToMap(endDotFile.getPath())).getMessage().contains("File does not have a valid extension"));
        }
    }

    @Test
    void testUnsupportedFileFormatThrowsException() {
        try (TempFile txtFile = new TempFile("test", ".txt")) {
            txtFile.write("key1: value1");
            assertTrue(assertThrows(IllegalArgumentException.class,
                    () -> FileUtils.loadFileToMap(txtFile.getPath())).getMessage().contains("Unsupported extension"));
        }
    }

    @Test
    void testNonExistentFileThrowsException() {
        Path nonExistent = Path.of("/path/to/nonexistent/file.json");
        assertTrue(assertThrows(IllegalArgumentException.class,
                () -> FileUtils.loadFileToMap(nonExistent)).getMessage().contains("Nonexistent file path provided"));
    }

    @Test
    void testWriteJsonFileFromMap() {
        var data = new LinkedHashMap<String, Object>();
        data.put("key1", "value1");
        data.put("key2", Map.of("nestedKey", "nestedValue"));

        try (TempFile tempJsonFile = new TempFile("write_test", ".json")) {
            FileUtils.writeMapToFile(tempJsonFile.getPath(), data);
            assertEquals(data, FileUtils.loadFileToMap(tempJsonFile.getPath()));
        }
    }

    @Test
    void testWriteYamlFileFromMap() {
        var data = new LinkedHashMap<String, Object>();
        data.put("key1", "value1");
        data.put("key2", Map.of("nestedKey", "nestedValue"));

        try (TempFile tempYamlFile = new TempFile("write_test", ".yaml")) {
            FileUtils.writeMapToFile(tempYamlFile.getPath(), data);
            assertEquals(data, FileUtils.loadFileToMap(tempYamlFile.getPath()));
        }
    }

    @Test
    void testWriteUnsupportedFormatThrowsException() {
        try (TempFile tempTxtFile = new TempFile("write_test", ".txt")) {
            var data = new LinkedHashMap<String, Object>();
            data.put("key", "value");

            assertTrue(assertThrows(IllegalArgumentException.class,
                    () -> FileUtils.writeMapToFile(tempTxtFile.getPath(), data)).getMessage().contains("Unsupported extension"));
        }
    }

    @Test
    void testLoadJsonFileToObject() {
        try (TempFile tempJsonFile = new TempFile("test", ".json")) {
            tempJsonFile.write("""
                    {
                      "name": "John",
                      "version": 1
                    }
                    """);
            var actual = FileUtils.loadFileToObject(tempJsonFile.getPath(), TestConfig.class);
            assertNotNull(actual);
            assertEquals(new TestConfig("John", 1), actual);
        }
    }

    @Test
    void testLoadYamlFileToObject() {
        try (TempFile tempYamlFile = new TempFile("test", ".yaml")) {
            tempYamlFile.write("""
                    name: "John"
                    version: 1
                    """);
            var actual = FileUtils.loadFileToObject(tempYamlFile.getPath(), TestConfig.class);
            assertNotNull(actual);
            assertEquals(new TestConfig("John", 1), actual);
        }
    }

    @Test
    void testLoadFileToObjectNegatives() {
        assertThrows(UncheckedIOException.class,
                () -> FileUtils.loadFileToObject(Paths.get(""), TestConfig.class));
        assertThrows(IllegalArgumentException.class,
                () -> FileUtils.loadFileToObject(Path.of("Wrong_name"), TestConfig.class));

        try (TempFile wrongExtFile = new TempFile("wrong", ".ext")) {
            wrongExtFile.write("dummy values");
            assertThrows(IllegalArgumentException.class,
                    () -> FileUtils.loadFileToObject(wrongExtFile.getPath(), TestConfig.class));
        }

        for (String ext : new String[]{".json", ".yaml", ".yml"}) {
            try (TempFile emptyFile = new TempFile("empty_file", ext)) {
                emptyFile.write("");
                assertThrows(UncheckedIOException.class,
                        () -> FileUtils.loadFileToObject(emptyFile.getPath(), TestConfig.class));
            }
        }
    }
}