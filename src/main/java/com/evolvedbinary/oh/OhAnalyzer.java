package com.evolvedbinary.oh;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

import java.io.IOException;
import java.io.Reader;

public class OhAnalyzer extends Analyzer {

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
//       The WhiteSpace tokenizer doesnt filter anything and also doesnt break on special characters
        final Tokenizer src = new WhitespaceTokenizer(reader);
//        final ClassicTokenizer src = new St(reader);

//        final TokenStream tok = new StandardFi

        TokenStream tok = new OhFilter(src);
//        TokenStream tok = new StandardFilter(getVersion(), src);
//        tok = new LowerCaseFilter(getVersion(), tok);
//        tok = new StopFilter(getVersion(), tok, stopwords);

        return new TokenStreamComponents(src, tok) {
            @Override
            protected void setReader(final Reader reader) throws IOException {
//                src.setMaxTokenLength(StandardAnalyzer.DEFAULT_MAX_TOKEN_LENGTH);
                super.setReader(reader);
            }
        };
    }
}
