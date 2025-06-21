package community.redrover.merge.testutils;

import lombok.Getter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
public final class TempFile implements AutoCloseable {

    private final Path path;

    public TempFile(String prefix, String suffix) {
        try {
            this.path = Files.createTempFile(prefix, suffix);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    String.format("Failed to create temp file with prefix '%s' and suffix '%s'", prefix, suffix), e);
        }
    }

    public void write(String content) {
        try {
            Files.writeString(path, content);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    String.format("Failed to write to temp file '%s'", path), e);
        }
    }

    @Override
    public void close() {
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new UncheckedIOException(
                    String.format("Failed to delete temp file '%s'", path), e);
        }
    }
}