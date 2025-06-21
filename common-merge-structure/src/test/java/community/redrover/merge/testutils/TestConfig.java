package community.redrover.merge.testutils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public final class TestConfig {
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