package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

public class Searcher {
    public static void main(String[] args) throws Exception{
        if (args.length != 2 )
        {
            throw new IllegalArgumentException();
        }

        String indexDir = args[0];
        String q = args[1];

        search(indexDir, q);
    }

    public static void search(String indexDir, String q) throws IOException, ParseException {
        DirectoryReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexDir)));
        IndexSearcher searcher = new IndexSearcher(reader);
        Analyzer analyzer = new StandardAnalyzer();
        QueryParser parser = new QueryParser("contents", analyzer);

        Query query = parser.parse(q);
        long start = System.currentTimeMillis();
        TopDocs results = searcher.search(query, 10);
        long end = System.currentTimeMillis();
        ScoreDoc[] hits = results.scoreDocs;

        System.out.println("Found " + results.totalHits + " document(s) (in " + (end -start) + " milliseconds");
        StoredFields storedFields = searcher.storedFields();
        int searchEnd = hits.length;
        for (int i = 0; i < searchEnd; i++) {
            Document doc = storedFields.document(hits[i].doc);
            String path = doc.get("fullpath");
            if (path != null) {
                  System.out.println((i + 1) + ". " + path);
                  String title = doc.get("filename");
                  if (title != null) {
                        System.out.println("   Title: " + title);
                      }
                } else {
                  System.out.println((i + 1) + ". " + "No path for this document");
                }
          }
    }
}
