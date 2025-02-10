package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Indexer {
    public static void main(String[] args) throws Exception{
        if (args.length != 2)
        {
            throw new IllegalArgumentException();
        }

        String indexDir = args[0];
        String dataDir = args[1];
        long start = System.currentTimeMillis();

        Indexer indexer = new Indexer(indexDir);
        int numIndexed;

        try
        {
            numIndexed = indexer.index(dataDir);
        }
        finally
        {
            indexer.close();
        }

        long end = System.currentTimeMillis();

        System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
    }

    private final IndexWriter writer;

    public Indexer(String indexDir) throws IOException{
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
        writer = new IndexWriter(dir, iwc);
    }

    public void close() throws IOException {
        writer.close();
    }

    public int index(String dataDir) throws Exception
    {
        File[] files = new File(dataDir).listFiles();
        assert files != null;
        for (File f : files)
        {
            if (!f.isDirectory())
            {
                indexFile(f);
            }
        }

        return writer.numRamDocs();
    }

    protected Document getDocument(File f) throws Exception {
        Document doc = new Document();
        doc.add(new TextField("contents", new FileReader(f)));
        doc.add(new StringField("filename", f.getName(), Field.Store.YES));
        doc.add(new StringField("fullpath", f.getCanonicalPath(), Field.Store.YES));

        return doc;
    }

    private void indexFile(File f) throws Exception
    {
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = getDocument(f);
        writer.addDocument(doc);
    }

}