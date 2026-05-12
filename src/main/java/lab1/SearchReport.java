package lab1;

import java.nio.file.Path;
import java.util.List;

public record SearchReport(
        String queryName,
        Path indexDirectory,
        String luceneQuery,
        long totalHits,
        long elapsedMillis,
        List<SearchHit> hits) {
}
