package com.evolvedbinary.lucene.analyzer;

import com.evolvedbinary.j8fu.function.ConsumerE;
import com.evolvedbinary.j8fu.function.FunctionE;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.nio.file.Path;

public interface LuceneHof {

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
