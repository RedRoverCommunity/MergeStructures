package community.redrover.merge.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;


public class FileUtils {

    public static Map<String, Object> loadFileToMap(Path path) throws IOException {
        File file = path.toFile();

        if (!file.exists()) {
            throw new IllegalArgumentException("Nonexisting file path provided: " + path);
        }

        if (file.length() == 0) {
            throw new UncheckedIOException("File is empty: " + path, new IOException());
        }

        String fileExtension = getFileExtension(file.getName());

        return switch (fileExtension.toLowerCase()) {
            case "json" -> parseJsonFile(file);
            case "yaml", "yml" -> parseYamlFile(file);
            default -> throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
        };
    }

    public static void writeMapToFile(Path filePath, Map<String, Object> data) throws IOException {
        String extension = getFileExtension(filePath.getFileName().toString()).toLowerCase();

        switch (extension) {
            case "json" -> {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), data);
            }
            case "yaml", "yml" -> {
                Yaml yaml = new Yaml();
                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    yaml.dump(data, writer);
                }
            }
            default -> throw new IllegalArgumentException("Unsupported file format: " + extension);
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
    public static <T> T loadFileToObject(String filePath, Class<T> clazz) throws IOException {
        if (clazz == null) {
            throw new IllegalArgumentException("Class cannot be null");
        }

        final String fileName = filePath.trim().toLowerCase();
        File file = getFile(fileName);

        final SupportedExtension EXTENSION = SupportedExtension.fromValue(getFileExtension(fileName));
        return EXTENSION.getObjectMapper().readValue(file, clazz);
    }

    private static File getFile(String fileName) throws IOException {
        Objects.requireNonNull(fileName, "File name cannot be null");

        if (fileName.isBlank()) {
            throw new IllegalArgumentException("File name cannot be null or empty");
        }

        Path path = Paths.get(fileName);

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

    private static Map<String, Object> parseJsonFile(File file) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        try {

            return objectMapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        } catch (IOException e) {
            throw new IOException("Error reading JSON file: " + file.getPath(), e);
        }
    }

    private static Map<String, Object> parseYamlFile(File file) throws IOException {
        Yaml yaml = new Yaml();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Map<String, Object> result = yaml.load(inputStream);
            if (result == null) {
                throw new IOException("YAML file is empty or invalid: " + file.getPath());
            }

            return result;
        } catch (IOException e) {
            throw new IOException("Error reading YAML file: " + file.getPath(), e);
        }
    }

    private static String getFileExtension(String filePath) {
        int lastDotIndex = filePath.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filePath.length() - 1) {
            throw new IllegalArgumentException("File does not have a valid extension: " + filePath);
        }

        return filePath.substring(lastDotIndex + 1);
    }
}