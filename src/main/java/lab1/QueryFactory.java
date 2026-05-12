package lab1;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.queryparser.flexible.standard.config.PointsConfig;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;

public final class QueryFactory {
    private QueryFactory() {
    }

    public static Query createBooleanQueryDirect() {
        Query life = new TermQuery(new Term(LuceneFields.CONTENTS, "life"));
        Query time = new TermQuery(new Term(LuceneFields.CONTENTS, "time"));
        Query man = new TermQuery(new Term(LuceneFields.CONTENTS, "man"));
        Query queen = new TermQuery(new Term(LuceneFields.CONTENTS, "queen"));

        BooleanQuery lifeAndTime = new BooleanQuery.Builder()
                .add(life, BooleanClause.Occur.MUST)
                .add(time, BooleanClause.Occur.MUST)
                .build();

        return new BooleanQuery.Builder()
                .add(lifeAndTime, BooleanClause.Occur.SHOULD)
                .add(man, BooleanClause.Occur.SHOULD)
                .add(queen, BooleanClause.Occur.MUST_NOT)
                .build();
    }

    public static Query createBooleanQueryParsed(Analyzer analyzer) throws Exception {
        QueryParser parser = new QueryParser(LuceneFields.CONTENTS, analyzer);
        return parser.parse("((life AND time) OR man) AND NOT queen");
    }

    public static Query createPointRangeQueryDirect(long lowerBytes, long upperBytes) {
        return LongPoint.newRangeQuery(LuceneFields.SIZE_BYTES, lowerBytes, upperBytes);
    }

    public static Query createPointRangeQueryParsed(Analyzer analyzer, String text) throws Exception {
        StandardQueryParser parser = new StandardQueryParser(analyzer);
        Map<String, PointsConfig> pointsConfig = new HashMap<>();
        pointsConfig.put(LuceneFields.SIZE_BYTES, new PointsConfig(NumberFormat.getInstance(), Long.class));
        parser.setPointsConfigMap(pointsConfig);
        return parser.parse(text, LuceneFields.SIZE_BYTES);
    }
}
