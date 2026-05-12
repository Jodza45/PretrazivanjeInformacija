package lab1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public final class Indexer {
    private Indexer() {
    }

    public static IndexReport createIndex(Path dataDirectory, Path indexDirectory, Analyzer analyzer) throws IOException {
        FileUtils.recreateDirectory(indexDirectory);
        List<Path> files = listTextFiles(dataDirectory);
        long start = System.nanoTime();

        try (Directory directory = FSDirectory.open(indexDirectory)) {
            IndexWriterConfig config = new IndexWriterConfig(analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
            try (IndexWriter writer = new IndexWriter(directory, config)) {
                for (Path file : files) {
                    writer.addDocument(createDocument(file));
                }
                writer.commit();
            }
        }

        long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;
        long indexSize = FileUtils.directorySize(indexDirectory);
        return new IndexReport(dataDirectory, indexDirectory, files.size(), elapsedMillis, indexSize);
    }

    private static Document createDocument(Path file) throws IOException {
        Document document = new Document();
        String contents = Files.readString(file, StandardCharsets.UTF_8);
        long size = Files.size(file);

        document.add(new TextField(LuceneFields.CONTENTS, contents, Field.Store.NO));
        document.add(new StringField(LuceneFields.FILENAME, file.getFileName().toString(), Field.Store.YES));
        document.add(new StringField(LuceneFields.FULL_PATH, file.toAbsolutePath().toString(), Field.Store.YES));
        document.add(new LongPoint(LuceneFields.SIZE_BYTES, size));
        document.add(new StoredField(LuceneFields.SIZE_BYTES_STORED, size));
        return document;
    }

    private static List<Path> listTextFiles(Path dataDirectory) throws IOException {
        try (Stream<Path> files = Files.walk(dataDirectory)) {
            return files
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase().endsWith(".txt"))
                    .sorted()
                    .toList();
        }
    }
}
