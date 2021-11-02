package com.evolvedbinary.oh;

import com.evolvedbinary.j8fu.function.ConsumerE;
import com.evolvedbinary.j8fu.function.FunctionE;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;



import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class OhIndexWriter {

    private static String ID_FIELD_NAME = "ID";
    private static String TEXT_FIELD_NAME = "Text";

    private static Path indexDir;

    public static void main(String[] args) throws IOException {
        indexDir = Paths.get("index");

        storeDocuments(
                "<doc id=\"1\">(S) Banquo Goa f 16</doc>",
                "<doc id=\"2\">(U) Banquo's S/S f-16</doc>",
                "<doc id=\"3\">(C) banquo's U.S.S.R. F-16</doc>",
                "<doc id=\"4\">(TS) banquo GOA F 16</doc>",
                "<doc id=\"5\">(S) Banquo’s f16</doc>",
                "<doc id=\"6\">(S) banquo’s F16</doc>"
        );
    }

    public static void storeDocuments(final Path tempDir) throws IOException {
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


    private static void storeDocuments(final String... xmlDocuments) throws IOException {
        final List<IdAndText> xmlDocumentsContents = extractIdAndText(xmlDocuments);
        final List<Document> documents = asLuceneDocuments(xmlDocumentsContents);

        try (final Analyzer analyzer = newAnalyzer()) {

            LuceneHof.withIndexWriter(indexDir, analyzer, indexWriter -> {
                for (final Document document : documents) {
                    indexWriter.addDocument(document);
                }
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

    private interface LuceneHof {

        static void withIndexWriter(final Path indexDir, final Analyzer analyzer, final ConsumerE<IndexWriter, IOException> indexWriterConsumer) throws IOException {
            try (final Directory dir = FSDirectory.open(indexDir.toFile())) {
                final IndexWriterConfig idxWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_4, analyzer);
                idxWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);

                try (final IndexWriter indexWriter = new IndexWriter(dir, idxWriterConfig)) {
                    indexWriterConsumer.accept(indexWriter);

                    indexWriter.commit();
                }
            }
        }

        static <T> T withDirectoryReader(final Path indexDir, final FunctionE<DirectoryReader, T, IOException> directoryReadingFunction) throws IOException {
            try (final Directory dir = FSDirectory.open(indexDir.toFile())) {
                try (final DirectoryReader directoryReader = DirectoryReader.open(dir)) {
                    return directoryReadingFunction.apply(directoryReader);
                }
            }
        }
    }
}
