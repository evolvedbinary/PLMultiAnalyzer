package com.lucene.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MyIndexSearcher {
    public static void main(String[] args) throws IOException, ParseException {
        Analyzer analyzer = new WhitespaceAnalyzer();

        Directory directory = new SimpleFSDirectory(new File("index"));
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser parser = new QueryParser( "fieldname", analyzer);
        Query query = parser.parse("Lucene");

        TopDocs docs = indexSearcher.search(query,1000);
        System.out.println("hits: " + docs.totalHits);
        System.out.println("Results:");
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            System.out.println(d.get("fieldname"));
        }
        directory.close();
        indexReader.close();
    }

}
