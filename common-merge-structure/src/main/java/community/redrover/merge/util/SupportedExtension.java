package community.redrover.merge.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

@Getter
public enum SupportedExtension {

    JSON(".json", createJsonMapper()),
    YAML(".yaml", createYamlMapper()),
    YML(".yml", createYamlMapper());

    private static final ObjectMapper JSON_MAPPER = new ObjectMapper();
    private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private final String value;
    private final ObjectMapper objectMapper;

    SupportedExtension(String value, ObjectMapper objectMapper) {
        this.value = Objects.requireNonNull(value, "Extension value cannot be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper cannot be null");
    }

    private static ObjectMapper createJsonMapper() {
        return JSON_MAPPER
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private static ObjectMapper createYamlMapper() {
        return YAML_MAPPER
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    public static SupportedExtension fromValue(String value) {
        return Arrays.stream(values())
                .filter(ext -> ext.getValue().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported extension: " + value));
    }
}
