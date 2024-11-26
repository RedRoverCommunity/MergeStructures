package community.redrover.merge.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.yaml.snakeyaml.Yaml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;


public class FileUtils {

    public static Map<String, Object> loadFileToMap(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists() || file.isDirectory()) {
            throw new IllegalArgumentException("Invalid file path provided: " + filePath);
        }

        if (file.length() == 0) {
            throw new IOException("File is empty: " + filePath);
        }

        String fileExtension = getFileExtension(filePath);
        switch (fileExtension.toLowerCase()) {
            case "json" -> {

                return parseJsonFile(file);
            }
            case "yaml", "yml" -> {

                return parseYamlFile(file);
            }
            default -> throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
        }
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