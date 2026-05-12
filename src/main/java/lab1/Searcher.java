package lab1;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

public final class Searcher {
    private Searcher() {
    }

    public static SearchReport search(Path indexDirectory, String queryName, Query query, int maxResults) throws IOException {
        try (FSDirectory directory = FSDirectory.open(indexDirectory);
                DirectoryReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            long start = System.nanoTime();
            TopDocs topDocs = searcher.search(query, maxResults);
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000L;

            List<SearchHit> hits = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = searcher.storedFields().document(scoreDoc.doc);
                hits.add(new SearchHit(
                        document.get(LuceneFields.FILENAME),
                        document.get(LuceneFields.FULL_PATH),
                        document.getField(LuceneFields.SIZE_BYTES_STORED).numericValue().longValue()));
            }

            return new SearchReport(queryName, indexDirectory, query.toString(), topDocs.totalHits.value, elapsedMillis, hits);
        }
    }
}
