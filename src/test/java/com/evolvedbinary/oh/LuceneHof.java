/*
 * The MIT License
 * Copyright © 2021 Evolved Binary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.evolvedbinary.oh;

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
