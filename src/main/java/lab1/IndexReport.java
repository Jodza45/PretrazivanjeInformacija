package lab1;

import java.nio.file.Path;

public record IndexReport(
        Path dataDirectory,
        Path indexDirectory,
        int documentCount,
        long elapsedMillis,
        long indexSizeBytes) {
}
