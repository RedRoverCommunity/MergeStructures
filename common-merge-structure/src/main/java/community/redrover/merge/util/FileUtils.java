package community.redrover.merge.util;

import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public class FileUtils {

    public static Map<String, Object> loadFileToMap(Path path) {
        try {
            File file = getFile(path);
            String fileExtension = getFileExtension(file.getName());
            SupportedExtension extension = SupportedExtension.fromValue(fileExtension);
            return extension.getObjectMapper().readValue(file, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read file: " + path, e);
        }
    }

    public static void writeMapToFile(Path filePath, Map<String, Object> data) {
        File file = filePath.toFile();
        String extension = getFileExtension(filePath.getFileName().toString());
        SupportedExtension ext = SupportedExtension.fromValue(extension);

        try {
            ext.getObjectMapper()
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, data);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to write file: " + filePath, e);
        }
    }

    /**
     * Load file to object
     * @param filePath Absolute file path and name to load
     * @param clazz Class to load to
     * @return  return deserialized object
     * @param <T>   Type of object to load to
     * @throws IOException if file is not found or cannot be read
     */
    public static <T> T loadFileToObject(Path filePath, Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        try {
            File file = getFile(filePath);
            String fileExtension = getFileExtension(file.getName());
            SupportedExtension extension = SupportedExtension.fromValue(fileExtension);
            return extension.getObjectMapper().readValue(file, clazz);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to parse file: " + filePath, e);
        }
    }

    private static File getFile(Path path) throws IOException {
        Objects.requireNonNull(path, "Path cannot be null");

        if (!Files.exists(path)) {
            throw new IllegalArgumentException("Nonexisting file path provided: " + path);
        } else if (!Files.isReadable(path)) {
            throw new UncheckedIOException(new IOException("File is not readable: " + path));
        } else if (!Files.isRegularFile(path)) {
            throw new UncheckedIOException(new IOException("File is not a regular file: " + path));
        } else if (Files.isDirectory(path)) {
            throw new UncheckedIOException(new IOException("File is a directory: " + path));
        } else if (Files.size(path) == 0) {
            throw new UncheckedIOException(new IOException("File is empty: " + path));
        }

        return path.toFile();
    }

    private static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            throw new IllegalArgumentException("File does not have a valid extension: " + filePath);
        }

        return filePath.substring(lastDotIndex + 1);
    }
}