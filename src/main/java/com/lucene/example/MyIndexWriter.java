package com.lucene.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

public class MyIndexWriter {
    public static void main(String[] args) throws IOException {
        Analyzer analyzer = new WhitespaceAnalyzer();
        Directory directory = new SimpleFSDirectory(new File("index"));
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,conf);

        // Document Creation
        Document doc = new Document();
        String text1 = "Lucene is an Information Retrieval library written in Java. lucene is an Information Retrieval library written in Java."; // => [Lucene] [lucene]
        String text2 = "lucene is an Information Retrieval library written in Java.";
        String text3 = "Lucene's is an Information Retrieval library written in Java."; // => [Lucene's] [Lucene] [lucene's] [lucene]
        String text4 = "Lucenes is an Information Retrieval library written in Java.";
        doc.add(new TextField("fieldname", text1, Field.Store.YES));
        indexWriter.addDocument(doc);
//        doc = new Document();
//        doc.add(new TextField("fieldname", text2, Field.Store.YES));
//        // Adding Document to index
//        indexWriter.addDocument(doc);
//        doc = new Document();
//        doc.add(new TextField("fieldname", text3, Field.Store.YES));
//        // Adding Document to index
//        indexWriter.addDocument(doc);
//        doc = new Document();
//        doc.add(new TextField("fieldname", text4, Field.Store.YES));
//        // Adding Document to index
//        indexWriter.addDocument(doc);

        indexWriter.close();
    }
}
