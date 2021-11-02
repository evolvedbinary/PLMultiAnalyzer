package com.evolvedbinary.oh;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;

public class ohIndexSearcher {
    private static String TEXT_FIELD_NAME = "Text";

    public static void main(String[] args) throws IOException, ParseException {
        Analyzer analyzer = new OhAnalyzer();

        Directory directory = new SimpleFSDirectory(new File("index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser parser = new QueryParser( TEXT_FIELD_NAME, analyzer);
        Query query = parser.parse("Goa");

        TopDocs docs = indexSearcher.search(query,1000);
        System.out.println("hits: " + docs.totalHits);
        System.out.println("Results:");
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            System.out.println(d.get(TEXT_FIELD_NAME));
        }
        directory.close();
        indexReader.close();
    }
}
