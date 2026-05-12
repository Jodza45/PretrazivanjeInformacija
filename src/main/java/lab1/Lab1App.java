package lab1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.Query;

public class Lab1App {
    private static final int PARTS_PER_ORIGINAL_FILE = 100;
    private static final int MAX_RESULTS = 10;

    public static void main(String[] args) throws Exception {
        Path projectRoot = Path.of("").toAbsolutePath();
        Path originalData = projectRoot.resolve("data").resolve("original");
        Path splitData = projectRoot.resolve("data").resolve("split");
        Path originalIndex = projectRoot.resolve("indexes").resolve("original");
        Path splitIndex = projectRoot.resolve("indexes").resolve("split");

        validateOriginalCollection(originalData);

        List<Path> splitFiles = FileSplitter.splitCollection(originalData, splitData, PARTS_PER_ORIGINAL_FILE);
        System.out.println("Kreirano delova: " + splitFiles.size());

        try (StandardAnalyzer analyzer = new StandardAnalyzer()) {
            IndexReport originalReport = Indexer.createIndex(originalData, originalIndex, analyzer);
            IndexReport splitReport = Indexer.createIndex(splitData, splitIndex, analyzer);

            printIndexReport("Originalna kolekcija", originalReport);
            printIndexReport("Podeljena kolekcija", splitReport);

            Query booleanDirect = QueryFactory.createBooleanQueryDirect();
            Query booleanParsed = QueryFactory.createBooleanQueryParsed(analyzer);
            long lowerBytes = 100L;
            long upperBytes = 500_000L;
            Query pointDirect = QueryFactory.createPointRangeQueryDirect(lowerBytes, upperBytes);
            Query pointParsed = QueryFactory.createPointRangeQueryParsed(analyzer,
                    "sizeBytes:[" + lowerBytes + " TO " + upperBytes + "]");

            System.out.println();
            System.out.println("Boolean tekstualni upit: ((life AND time) OR man) AND NOT queen");
            System.out.println("PointRange tekstualni upit: sizeBytes:[" + lowerBytes + " TO " + upperBytes + "]");

            runQueryOnBothIndexes("BooleanQuery direktno", booleanDirect, originalIndex, splitIndex);
            runQueryOnBothIndexes("BooleanQuery parser", booleanParsed, originalIndex, splitIndex);
            runQueryOnBothIndexes("PointRangeQuery direktno", pointDirect, originalIndex, splitIndex);
            runQueryOnBothIndexes("PointRangeQuery parser", pointParsed, originalIndex, splitIndex);
        }
    }

    private static void validateOriginalCollection(Path originalData) throws Exception {
        if (!Files.isDirectory(originalData)) {
            throw new IllegalStateException("Nedostaje folder: " + originalData);
        }
        try (var files = Files.list(originalData)) {
            long count = files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .count();
            if (count < 4) {
                throw new IllegalStateException("U data/original moraju da postoje bar 4 .txt fajla.");
            }
        }
    }

    private static void printIndexReport(String title, IndexReport report) {
        System.out.println();
        System.out.println(title);
        System.out.println("  Direktorijum podataka: " + report.dataDirectory());
        System.out.println("  Direktorijum indeksa: " + report.indexDirectory());
        System.out.println("  Broj dokumenata: " + report.documentCount());
        System.out.println("  Vreme kreiranja indeksa: " + report.elapsedMillis() + " ms");
        System.out.println("  Velicina indeksa: " + report.indexSizeBytes() + " B");
    }

    private static void runQueryOnBothIndexes(String queryName, Query query, Path originalIndex, Path splitIndex) throws Exception {
        printSearchReport(Searcher.search(originalIndex, queryName + " / originalni indeks", query, MAX_RESULTS));
        printSearchReport(Searcher.search(splitIndex, queryName + " / podeljeni indeks", query, MAX_RESULTS));
    }

    private static void printSearchReport(SearchReport report) {
        System.out.println();
        System.out.println(report.queryName());
        System.out.println("  Indeks: " + report.indexDirectory());
        System.out.println("  Lucene Query objekat: " + report.luceneQuery());
        System.out.println("  Ukupno pogodaka: " + report.totalHits());
        System.out.println("  Vreme pretrage: " + report.elapsedMillis() + " ms");
        for (SearchHit hit : report.hits()) {
            System.out.printf("    size=%d file=%s%n", hit.sizeBytes(), hit.filename());
        }
    }
}
