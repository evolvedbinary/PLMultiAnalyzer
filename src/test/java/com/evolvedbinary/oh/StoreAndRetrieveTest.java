package com.evolvedbinary.oh;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.evolvedbinary.oh.LuceneHof.withDirectoryReader;
import static com.evolvedbinary.oh.LuceneHof.withIndexWriter;
import static org.junit.jupiter.api.Assertions.fail;

public class StoreAndRetrieveTest {

    private static String ID_FIELD_NAME = "ID";
    private static String TEXT_FIELD_NAME = "Text";

    private static Path indexDir;

    @BeforeAll
    public static void storeDocuments(final @TempDir Path tempDir) throws IOException {
        indexDir = tempDir;

        storeDocuments(
                "<doc id=\"1\">(S) Banquo Goa f 16</doc>",
                "<doc id=\"2\">(U) Banquo's S/S f-16</doc>",
                "<doc id=\"3\">(C) banquo's U.S.S.R. F-16</doc>",
                "<doc id=\"4\">(TS) banquo GOA F 16</doc>",
                "<doc id=\"5\">(S) Banquo’s f16</doc>",
                "<doc id=\"6\">(S) banquo’s F16</doc>"
        );
    }

    /**
     * When searching for the term Banquo,
     * Results should include doc 5 (Banquo’s i.e. with a 'Right Single Quotation Mark')
     */
    @Test
    public void banquoSearchResultsIncludeBanquoRightSingleQuotationMarkS() throws ParseException, IOException {
        final List<SearchResult> results = search("Banquo");
        assertIncludesDocument("<doc id=\"5\">(S) Banquo’s f16</doc>", results);
    }

    /**
     * When searching for the term banquo's (i.e. with an 'Apostrophe'),
     * Results should include docs 2, 3, 5, and 6
     */
    @Test
    public void banquoApostropheSSearchResultsIncludeAllWithS() throws ParseException, IOException {
        final List<SearchResult> results = search("banquo's");
        assertIncludesDocument("<doc id=\"2\">(U) Banquo's S/S f-16</doc>", results);
        assertIncludesDocument("<doc id=\"3\">(C) banquo's U.S.S.R. F-16</doc>", results);
        assertIncludesDocument("<doc id=\"5\">(S) Banquo’s f16</doc>", results);
        assertIncludesDocument("<doc id=\"6\">(S) banquo’s F16</doc>", results);

        /*
         TODO(AR) the fact that 2 and 3 work above tells us that apostrophes are being removed,
         we can see comments about this in StandardFilter which is used by StandardAnalyzer
         we can also see that RightSingleQuotationMark does not get removed - can we extend/adapt StandardFilter for this?
         */
    }


    private static void assertIncludesDocument(final String xmlDocument, final List<SearchResult> searchResults) {
        final IdAndText expectedIdAndText = extractIdAndText(xmlDocument).get(0);
        for (final SearchResult searchResult : searchResults) {
            if (expectedIdAndText.equals(searchResult.idAndText)) {
                return;
            }
        }
        fail("Search Results did not include XML Document: " + xmlDocument);
    }

    /**
     *
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

    private static void storeDocuments(final String... xmlDocuments) throws IOException {
        final List<IdAndText> xmlDocumentsContents = extractIdAndText(xmlDocuments);
        final List<Document> documents = asLuceneDocuments(xmlDocumentsContents);

        try (final Analyzer analyzer = newAnalyzer()) {

            withIndexWriter(indexDir, analyzer, indexWriter -> {
                for (final Document document : documents) {
                    indexWriter.addDocument(document);
                }
            });
        }
    }

    private static List<SearchResult> search(final String queryString) throws ParseException, IOException {
        try (final Analyzer analyzer = newAnalyzer()) {

            final QueryParser queryParser = new QueryParser(TEXT_FIELD_NAME, analyzer);
            final Query query = queryParser.parse(queryString);

            return withDirectoryReader(indexDir, directoryReader -> {

                final IndexSearcher indexSearcher = new IndexSearcher(directoryReader);
                final TopDocs docs = indexSearcher.search(query, 1000);  //TODO(AR) remove the 1000 top docs!

                final List<SearchResult> searchResults = new ArrayList<>();

                for (int i = 0; i < docs.totalHits; i++) {
                    final Document document = indexSearcher.doc(docs.scoreDocs[i].doc);

                    // reconstruct ID and Text from stored fields in Index
                    final int id = document.getField(ID_FIELD_NAME).numericValue().intValue();
                    final String text = document.getField(TEXT_FIELD_NAME).stringValue();

                    final IdAndText idAndText = new IdAndText(id, text);
                    final SearchResult searchResult = new SearchResult(idAndText, docs.scoreDocs[i].score);
                    searchResults.add(searchResult);
                }

                return searchResults;
            });
        }
    }

    private static Analyzer newAnalyzer() {
        // TODO(AR) the analyzer is the bit we need to customise
        return new OhAnalyzer();
    }

    private static List<Document> asLuceneDocuments(final List<IdAndText> xmlDocumentsContents) {
        final List<Document> documents = new ArrayList<>(xmlDocumentsContents.size());

        for (final IdAndText xmlDocumentContent : xmlDocumentsContents) {
            final Document document = new Document();

            document.add(new IntField(ID_FIELD_NAME, xmlDocumentContent.id, Field.Store.YES));
            document.add(new TextField(TEXT_FIELD_NAME, xmlDocumentContent.text, Field.Store.YES));

            documents.add(document);
        }

        return documents;
    }

    private static List<IdAndText> extractIdAndText(final String... xmlDocuments) {
        final List<IdAndText> idAndTexts = new ArrayList<>(xmlDocuments.length);

        final Pattern xmlDocumentPattern = Pattern.compile("<doc id=\"([0-9]+)\">([^<]+)</doc>");
        Matcher xmlDocumentMatcher = null;
        for (final String xmlDocument : xmlDocuments) {
            // setup the pattern matcher
            if (xmlDocumentMatcher == null) {
                xmlDocumentMatcher = xmlDocumentPattern.matcher(xmlDocument);
            } else {
                xmlDocumentMatcher.reset(xmlDocument);
            }

            if (!xmlDocumentMatcher.matches()) {
                throw new IllegalArgumentException("XML Document does not match expected format: " + xmlDocument);
            }

            final int id = Integer.parseInt(xmlDocumentMatcher.group(1));
            final String text = xmlDocumentMatcher.group(2);

            final IdAndText idAndText = new IdAndText(id, text);
            idAndTexts.add(idAndText);
        }

        return idAndTexts;
    }

    private static class SearchResult {
        final IdAndText idAndText;
        final float score;

        public SearchResult(final IdAndText idAndText, final float score) {
            this.idAndText = idAndText;
            this.score = score;
        }
    }

    private static class IdAndText {
        final int id;
        final String text;

        public IdAndText(final int id, final String text) {
            this.id = id;
            this.text = text;
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }

            if (other == null || getClass() != other.getClass()) {
                return false;
            }

            final IdAndText idAndText = (IdAndText) other;

            if (id != idAndText.id) {
                return false;
            }

            return text.equals(idAndText.text);
        }
    }
}
