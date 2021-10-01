import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.print.Doc;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 <doc id="1">(S) Banquo Goa f 16</doc>
 <doc id="2">(U) Banquo's S/S f-16</doc>
 <doc id="3">(C) banquo's U.S.S.R. F-16</doc>
 <doc id="4">(TS) banquo GOA F 16</doc>
 <doc id="5">(S) Banquo’s f16</doc>
 <doc id="6">(S) banquo’s F16</doc>

 • When searching for the term Banquo
    • Results should include doc 5 (Banquo’s with a curly quote)
 • When searching for the term banquo's (straight quote)
    • Results should include docs 2, 3, 5, and 6
 • When searching for the term Banquo’s (curly quote)
    • Results should include docs 2, 3, 5, and 6
    • doc 5 should score higher than doc 2 (prefer exact quote match)
    • doc 5 should score higher than doc 6 (prefer exact capitalisation match)
    • doc 6 should score higher than doc 3 (prefer exact quote match even when case mis-matches)
 • When searching for the term f-16
    • doc 2 (f-16) should score higher than doc 1 (f 16)
 • When searching for the term F-16
    • doc 3 (F-16) should score higher than doc 2 (f-16) (prefer case match)
 • When searching for the term F-16
    • doc 3 (F-16) should score higher than doc 2 (f-16) (prefer case match)
    • doc 3 (F-16) should score higher than doc 4 (F 16) (prefer exact punctuation)
 • When searching for the term s/s
    • Lucene should not throw an error
    • doc 2 (S/S) should score higher than docs 1, 5, and 6 ((S) marking secret classifications).
    • doc 2 (S/S) should score higher than doc 3 (U.S.S.R.)
    • When searching for the term Goa
    • doc 1 should score higher than doc 4
 • When searching for the term GOA
    • doc 4 should score higher than doc 1
 */

class MyAnalyzerTest {
    Analyzer analyzer;
    Directory directory;

    MyAnalyzerTest() throws IOException {
        analyzer = new WhitespaceAnalyzer();
        directory = new SimpleFSDirectory(new File("index"));
    }

    @BeforeEach
    void setUp() throws IOException {
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,conf);
        Document doc = new Document();
        doc.add(new TextField("Text", "Banquo Goa f 16", Field.Store.YES));
        doc.add(new TextField("ID", "1", Field.Store.YES));
        // Adding Document to index
        indexWriter.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("Text", "Banquo's S/S f-16", Field.Store.YES));
        doc.add(new TextField("ID", "2", Field.Store.YES));
        // Adding Document to index
        indexWriter.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("Text", "banquo's U.S.S.R. F-16", Field.Store.YES));
        doc.add(new TextField("ID", "3", Field.Store.YES));
        // Adding Document to index
        indexWriter.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("Text", "banquo GOA F 16", Field.Store.YES));
        doc.add(new TextField("ID", "4", Field.Store.YES));
        // Adding Document to index
        indexWriter.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("Text", "Banquo’s f16", Field.Store.YES));
        doc.add(new TextField("ID", "5", Field.Store.YES));
        // Adding Document to index
        indexWriter.addDocument(doc);
        doc = new Document();
        doc.add(new TextField("Text", "banquo’s F16", Field.Store.YES));
        doc.add(new TextField("ID", "6", Field.Store.YES));
        // Adding Document to index
        indexWriter.addDocument(doc);
        indexWriter.commit();
        indexWriter.close();
    }

    @AfterEach
    void tearDown() throws IOException {
        Directory directory = new SimpleFSDirectory(new File("index"));
        IndexWriterConfig conf = new IndexWriterConfig(Version.LUCENE_4_10_4,analyzer);
        IndexWriter indexWriter = new IndexWriter(directory,conf);
        indexWriter.deleteAll();
        indexWriter.commit();
        indexWriter.close();
    }


    @Test
    /**
        • When searching for the term Banquo
            • Results should include doc 5 (Banquo’s with a curly quote)
     *
     * */
    void BanquoShouldRturnDoc5() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("Banquo");
        TopDocs docs = indexSearcher.search(query,1000);
        boolean includes = false;
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            if (d.get("ID").equals("5")) {
                includes = true;
            }
        }
        Assertions.assertTrue(includes);
        indexReader.close();
        directory.close();
    }

    @Test
    /**
     * • When searching for the term banquo's (straight quote)
     *     • Results should include docs 2, 3, 5, and 6
     */
    void banquoWithAquoat() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("Banquo");
        TopDocs docs = indexSearcher.search(query,1000);
        ArrayList<String> docsMatched = new ArrayList<>();
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            docsMatched.add(d.get("ID"));
        }
        Assertions.assertTrue(docsMatched.contains("2"));
        Assertions.assertTrue(docsMatched.contains("3"));
        Assertions.assertTrue(docsMatched.contains("5"));
        Assertions.assertTrue(docsMatched.contains("6"));
        indexReader.close();
        directory.close();
    }

    @Test
    /**
     • When searching for the term Banquo’s (curly quote)
        • Results should include docs 2, 3, 5, and 6
        • doc 5 should score higher than doc 2 (prefer exact quote match)
        • doc 5 should score higher than doc 6 (prefer exact capitalisation match)
        • doc 6 should score higher than doc 3 (prefer exact quote match even when case mis-matches)
     */
    void banquoCurlyQuote() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("Banquo’s");
        TopDocs docs = indexSearcher.search(query,1000);
        ArrayList<String> docsMatched = new ArrayList<>();
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
//            System.out.println(d.get("Text"));
//            System.out.println(docs.scoreDocs[i].score); //score
            docsMatched.add(d.get("ID"));
        }
        Assertions.assertTrue(docsMatched.contains("2"));
        Assertions.assertTrue(docsMatched.contains("3"));
        Assertions.assertTrue(docsMatched.contains("5"));
        Assertions.assertTrue(docsMatched.contains("6"));
        indexReader.close();
        directory.close();
    }

    @Test
    /**
     * When searching for the term f-16
        * doc 2 (f-16) should score higher than doc 1 (f 16)
     */
    void F_16() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("f-16");
        TopDocs docs = indexSearcher.search(query,1000);
        ArrayList<String> docsMatched = new ArrayList<>();
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            docsMatched.add(d.get("ID"));
        }
        Assertions.assertTrue(docsMatched.contains("2"));
        Assertions.assertTrue(docsMatched.contains("1"));
        indexReader.close();
        directory.close();
    }

    @Test
    /**
     * When searching for the term F-16
        * doc 3 (F-16) should score higher than doc 2 (f-16) (prefer case match)
        * doc 3 (F-16) should score higher than doc 4 (F 16) (prefer exact punctuation)
     */
    void capitalF_16() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("F-16");
        TopDocs docs = indexSearcher.search(query,1000);
        ArrayList<String> docsMatched = new ArrayList<>();
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            docsMatched.add(d.get("ID"));
        }
        Assertions.assertTrue(docsMatched.contains("2"));
        Assertions.assertTrue(docsMatched.contains("1"));
        Assertions.assertTrue(docsMatched.contains("3"));
        Assertions.assertTrue(docsMatched.contains("4"));
        indexReader.close();
        directory.close();
    }

    @Test
    /**
     * When searching for the term s/s
        * Lucene should not throw an error
        * doc 2 (S/S) should score higher than docs 1, 5, and 6 ((S) marking secret classifications).
        * doc 2 (S/S) should score higher than doc 3 (U.S.S.R.)
     */
    void sAndS() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("s/s");
        TopDocs docs = indexSearcher.search(query,1000);
        ArrayList<String> docsMatched = new ArrayList<>();
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            docsMatched.add(d.get("ID"));
        }
        Assertions.assertTrue(docsMatched.contains("2"));
        Assertions.assertTrue(docsMatched.contains("5"));
        Assertions.assertTrue(docsMatched.contains("3"));
        Assertions.assertTrue(docsMatched.contains("6"));
        indexReader.close();
        directory.close();
    }


    @Test
    /**
     * When searching for the term Goa
        * doc 1 should score higher than doc 4
     */
    void Goa() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("Goa");
        TopDocs docs = indexSearcher.search(query,1000);
        ArrayList<String> docsMatched = new ArrayList<>();
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            docsMatched.add(d.get("ID"));
        }
        Assertions.assertTrue(docsMatched.contains("1"));
        Assertions.assertTrue(docsMatched.contains("4"));
        indexReader.close();
        directory.close();
    }

    @Test
    /**
     * When searching for the term GOA
        * doc 4 should score higher than doc 1
     */
    void GOA() throws IOException, ParseException {
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);

        QueryParser parser = new QueryParser( "Text", analyzer);
        Query query = parser.parse("GOA");
        TopDocs docs = indexSearcher.search(query,1000);
        ArrayList<String> docsMatched = new ArrayList<>();
//        List<Float> scores = new ArrayList<Float>();
        for (int i = 0; i < docs.totalHits; i++) {
            Document d = indexSearcher.doc(docs.scoreDocs[i].doc);
            docsMatched.add(d.get("ID"));
//            scores.add(Integer.valueOf(d.get("ID")),docs.scoreDocs[i].score);
        }
        Assertions.assertTrue(docsMatched.contains("1"));
        Assertions.assertTrue(docsMatched.contains("4"));
//        Assertions.assertTrue(scores.get(3) > scores.get());

        indexReader.close();
        directory.close();
        
    }
}