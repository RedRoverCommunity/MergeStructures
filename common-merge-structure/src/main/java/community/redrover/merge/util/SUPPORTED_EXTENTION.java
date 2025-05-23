package community.redrover.merge.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.Getter;

@Getter
public enum SUPPORTED_EXTENTION {

    JSON(".json", new ObjectMapper()),
    YAML(".yaml", new ObjectMapper(new YAMLFactory())),
    YML(".yml", new ObjectMapper(new YAMLFactory()));

    private final String value;
    private final ObjectMapper objectMapper;

    SUPPORTED_EXTENTION(String value, ObjectMapper objectMapper) {
        this.value = value;
        this.objectMapper = objectMapper;
    }

}
