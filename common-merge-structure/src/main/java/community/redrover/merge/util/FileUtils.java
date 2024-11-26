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

        String fileExtension = getFileExtension(filePath);
        if ("json".equalsIgnoreCase(fileExtension)) {
            ObjectMapper objectMapper = new ObjectMapper();

            return objectMapper.readValue(file, new com.fasterxml.jackson.core.type.TypeReference<>() {});
        } else if ("yaml".equalsIgnoreCase(fileExtension) || "yml".equalsIgnoreCase(fileExtension)) {
            Yaml yaml = new Yaml();
            try (FileInputStream inputStream = new FileInputStream(filePath)) {

                return yaml.load(inputStream);
            } catch (IOException e) {
                throw new IOException("Error reading file: " + filePath, e);
            }
        } else {
            throw new IllegalArgumentException("Unsupported file format: " + fileExtension);
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
