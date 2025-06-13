package community.redrover.merge.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.jupiter.api.Assertions;
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

    private Path createTempFileWithContent(String prefix, String suffix, String content) {
        try {
            Path tempFile = Files.createTempFile(prefix, suffix);
            Files.writeString(tempFile, content);
            return tempFile;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void deleteTempFile(Path file) {
        assertDoesNotThrow(() -> Files.delete(file), "Failed to delete temp file: " + file);
    }

    private void deleteTempFileIfExists(String prefix, String suffix) {
        try {
            Files.deleteIfExists(Path.of(prefix + suffix));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to delete existing temp file: " + prefix + suffix, e);
        }
    }

    @Test
    void testEmptyFileThrowsException() {
        Path emptyFile = createTempFileWithContent("empty_file", ".test", "");
        try {
            IllegalStateException exception = assertThrows(
                    IllegalStateException.class,
                    () -> FileUtils.loadFileToMap(emptyFile)
            );

            assertTrue(exception.getMessage().contains("File is empty"));
        } finally {
            deleteTempFile(emptyFile);
        }
    }

    @Test
    void testValidJsonFileReturnsExpectedMap() {
        Path tempJsonFile = createTempFileWithContent("test", ".json", """
                {
                  "key1": "value1",
                  "key2": {
                    "nestedKey": "nestedValue"
                  }
                }
                """);
        try {
            LinkedHashMap<String, Object> result = FileUtils.loadFileToMap(tempJsonFile);

            assertEquals(2, result.size());
            assertEquals("value1", result.get("key1"));
            assertEquals("nestedValue", ((LinkedHashMap<?, ?>) result.get("key2")).get("nestedKey"));
        } finally {
            deleteTempFile(tempJsonFile);
        }
    }

    @Test
    void testValidYamlFileReturnsExpectedMap() {
        Path tempYamlFile = createTempFileWithContent("test", ".yaml", """
                key1: value1
                key2:
                  nestedKey: nestedValue
                """);
        try {
            LinkedHashMap<String, Object> result = FileUtils.loadFileToMap(tempYamlFile);

            assertEquals(2, result.size());
            assertEquals("value1", result.get("key1"));
            assertEquals("nestedValue", ((LinkedHashMap<?, ?>) result.get("key2")).get("nestedKey"));
        } finally {
            deleteTempFile(tempYamlFile);
        }
    }

    @Test
    void testFileWithoutExtensionThrowsException() {
        Path noExtTempFile = createTempFileWithContent("test", "", "key1: value1");
        try {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> FileUtils.loadFileToMap(noExtTempFile));

            assertTrue(exception.getMessage().contains("File does not have a valid extension"));
        } finally {
            deleteTempFile(noExtTempFile);
        }
    }

    @Test
    void testFileWithEndingDotThrowsException() {
        Path endDotTempFile = createTempFileWithContent("test", ".", "key1: value1");
        try {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> FileUtils.loadFileToMap(endDotTempFile)
            );

            assertTrue(exception.getMessage().contains("File does not have a valid extension"));
        } finally {
            deleteTempFile(endDotTempFile);
        }
    }

    @Test
    void testUnsupportedFileFormatThrowsException() {
        Path tempTxtFile = createTempFileWithContent("test", ".txt", "key1: value1");
        try {
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class, () -> FileUtils.loadFileToMap(tempTxtFile));

            assertTrue(exception.getMessage().contains("Unsupported extension"));
        } finally {
            deleteTempFile(tempTxtFile);
        }
    }

    @Test
    void testNonExistentFileThrowsException() {
        Path nonExistentFilePath = Path.of("/path/to/nonexistent/file.json");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> FileUtils.loadFileToMap(nonExistentFilePath));

        assertTrue(exception.getMessage().contains("Nonexisting file path provided"));
    }

    @Test
    void testWriteJsonFileFromMap() {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("key1", "value1");
        data.put("key2", Map.of("nestedKey", "nestedValue"));

        try {
            Path tempJsonFile = Files.createTempFile("write_test", ".json");
            try {
                FileUtils.writeMapToFile(tempJsonFile, data);
                assertEquals(data, FileUtils.loadFileToMap(tempJsonFile));
            } finally {
                deleteTempFile(tempJsonFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void testWriteYamlFileFromMap() {
        LinkedHashMap<String, Object> data = new LinkedHashMap<>();
        data.put("key1", "value1");
        data.put("key2", Map.of("nestedKey", "nestedValue"));

        try {
            Path tempYamlFile = Files.createTempFile("write_test", ".yaml");
            try {
                FileUtils.writeMapToFile(tempYamlFile, data);
                assertEquals(data, FileUtils.loadFileToMap(tempYamlFile));
            } finally {
                deleteTempFile(tempYamlFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void testWriteUnsupportedFormatThrowsException() {
        try {
            Path tempTxtFile = Files.createTempFile("write_test", ".txt");
            try {
                LinkedHashMap<String, Object> data = new LinkedHashMap<>();
                data.put("key", "value");

                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () ->
                        FileUtils.writeMapToFile(tempTxtFile, data));

                assertTrue(ex.getMessage().contains("Unsupported extension"));
            } finally {
                deleteTempFile(tempTxtFile);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Test
    void testLoadJsonFileToObject() {
        Path tempJsonFile = createTempFileWithContent("test", ".json", """
                {
                  "name": "John",
                  "version": 1
                }
                """);
        try {
            final TestConfig expectedConfig = new TestConfig("John", 1);

            TestConfig actualConfig = FileUtils.loadFileToObject(tempJsonFile, TestConfig.class);

            Assertions.assertNotNull(actualConfig);
            Assertions.assertEquals(expectedConfig, actualConfig);
        } finally {
            deleteTempFile(tempJsonFile);
        }
    }

    @Test
    void testLoadYamlFileToObject() {
        Path tempYamlFile = createTempFileWithContent("test", ".yaml", """
                name: "John"
                version: 1
                """);
        try {
            TestConfig actualConfig = FileUtils.loadFileToObject(tempYamlFile, TestConfig.class);

            Assertions.assertNotNull(actualConfig);
            Assertions.assertEquals(new TestConfig("John", 1), actualConfig);
        } finally {
            deleteTempFile(tempYamlFile);
        }
    }

    @Test
    void testLoadFileToObjectNegatives() {
        assertThrows(IllegalStateException.class, () -> FileUtils.loadFileToObject(Paths.get(""), TestConfig.class));

        assertThrows(IllegalArgumentException.class, () -> FileUtils.loadFileToObject(Path.of("Wrong_name"), TestConfig.class));

        final Path wrongExtensionFile = createTempFileWithContent("wrong", ".ext", "dummy values");
        assertThrows(IllegalArgumentException.class, () -> FileUtils.loadFileToObject(wrongExtensionFile, TestConfig.class));
        deleteTempFileIfExists("wrong", ".ext");

        final Path emptyJsonFile = createTempFileWithContent("empty_file", ".json", "");
        assertThrows(IllegalStateException.class, () -> FileUtils.loadFileToObject(emptyJsonFile, TestConfig.class));
        deleteTempFileIfExists("empty_file", ".json");

        final Path emptyYamlFile1 = createTempFileWithContent("empty_file", ".yaml", "");
        assertThrows(IllegalStateException.class, () -> FileUtils.loadFileToObject(emptyYamlFile1, TestConfig.class));
        deleteTempFileIfExists("empty_file", ".yaml");

        final Path emptyYamlFile2 = createTempFileWithContent("empty_file", ".yml", "");
        assertThrows(IllegalStateException.class, () -> FileUtils.loadFileToObject(emptyYamlFile2, TestConfig.class));
        deleteTempFileIfExists("empty_file", ".yml");
    }
}