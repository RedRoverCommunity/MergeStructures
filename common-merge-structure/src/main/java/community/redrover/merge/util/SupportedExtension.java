package community.redrover.merge.util;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import lombok.Getter;
import java.util.Arrays;
import java.util.Objects;

@Getter
public enum SupportedExtension {

    JSON("json", JsonMapper.builder()
            .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build()
    ),

    YAML("yaml", createYamlMapper()),
    YML("yml",  createYamlMapper());

    private final String value;
    private final ObjectMapper objectMapper;

    SupportedExtension(String value, ObjectMapper objectMapper) {
        this.value = Objects.requireNonNull(value, "Extension value cannot be null");
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper cannot be null");
    }

    private static ObjectMapper createYamlMapper() {
        YAMLFactory yamlFactory = new YAMLFactory()
                .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER);

        return YAMLMapper.builder(yamlFactory)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
    }

    public static SupportedExtension fromValue(String extValue) {
        return Arrays.stream(values())
                .filter(supportedExtension -> supportedExtension.value.equalsIgnoreCase(extValue))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported extension: " + extValue));
    }
}