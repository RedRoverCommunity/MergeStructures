package community.redrover.merge.util;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;


public class FileUtilsTest {

    private Path createTempFileWithContent(String prefix, String suffix, String content) throws IOException {
        Path tempFile = Files.createTempFile(prefix, suffix);
        Files.writeString(tempFile, content);

        return tempFile;
    }

    private void deleteTempFile(Path file) {
        assertDoesNotThrow(() -> Files.delete(file), "Failed to delete temp file: " + file);
    }

    @Test
    void testEmptyFileThrowsIOException() throws IOException {
        Path emptyFile = createTempFileWithContent("empty_file", ".test", "");

        IOException exception = assertThrows(
                IOException.class, () -> FileUtils.loadFileToMap(emptyFile.toString()));
        assertTrue(exception.getMessage().contains("File is empty"));

        deleteTempFile(emptyFile);
    }

    @Test
    void testValidJsonFileReturnsExpectedMap() throws IOException {
        Path tempJsonFile = createTempFileWithContent("test", ".json", """
                {
                  "key1": "value1",
                  "key2": {
                    "nestedKey": "nestedValue"
                  }
                }
                """);

        Map<String, Object> result = FileUtils.loadFileToMap(tempJsonFile.toString());

        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("nestedValue", ((Map<?, ?>) result.get("key2")).get("nestedKey"));

        deleteTempFile(tempJsonFile);
    }

    @Test
    void testValidYamlFileReturnsExpectedMap() throws IOException {
        Path tempYamlFile = createTempFileWithContent("test", ".yaml", """
                key1: value1
                key2:
                  nestedKey: nestedValue
                """);

        Map<String, Object> result = FileUtils.loadFileToMap(tempYamlFile.toString());

        assertEquals(2, result.size());
        assertEquals("value1", result.get("key1"));
        assertEquals("nestedValue", ((Map<?, ?>) result.get("key2")).get("nestedKey"));

        deleteTempFile(tempYamlFile);
    }

    @Test
    void testFileWithoutExtensionThrowsException() throws IOException {
        Path noExtTempFile = createTempFileWithContent("test", "", "key1: value1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> FileUtils.loadFileToMap(noExtTempFile.toString()));
        assertTrue(exception.getMessage().contains("File does not have a valid extension"));

        deleteTempFile(noExtTempFile);
    }

    @Test
    void testFileWithEndingDotThrowsException() throws IOException {
        Path endDotTempFile = createTempFileWithContent("test", ".", "key1: value1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> FileUtils.loadFileToMap(endDotTempFile.toString()));
        assertTrue(exception.getMessage().contains("File does not have a valid extension"));

        deleteTempFile(endDotTempFile);
    }

    @Test
    void testUnsupportedFileFormatThrowsException() throws IOException {
        Path tempTxtFile = createTempFileWithContent("test", ".txt", "key1: value1");

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> FileUtils.loadFileToMap(tempTxtFile.toString()));
        assertTrue(exception.getMessage().contains("Unsupported file format"));

        deleteTempFile(tempTxtFile);
    }

    @Test
    void testNonExistentFileThrowsException() {
        String nonExistentFilePath = "/path/to/nonexistent/file.json";

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class, () -> FileUtils.loadFileToMap(nonExistentFilePath));
        assertTrue(exception.getMessage().contains("Invalid file path provided"));
    }
}
