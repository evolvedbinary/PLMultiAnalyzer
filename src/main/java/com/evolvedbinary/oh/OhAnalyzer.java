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

import it.unimi.dsi.fastutil.chars.CharArraySet;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;

import java.io.IOException;
import java.io.Reader;

public class OhAnalyzer extends Analyzer {
    private final char[] punctuationDictionary;
    private final int minimumTermLength;

    /**
     * @param punctuationDictionary the dictionary of punctuation to use
     *     for decomposition.
     * @param minimumTermLength the minimum length of any decomposed term,
     *     any smaller decomposed terms will be discarded. Set to 0 to
     *     indicate no minimum.
     */
    public OhAnalyzer(final char[] punctuationDictionary, final int minimumTermLength) {
        super();
        this.punctuationDictionary = punctuationDictionary;
        this.minimumTermLength = minimumTermLength;
    }

    /**
     * @param punctuationDictionary the dictionary of punctuation to use
     *     for decomposition.
     * @param minimumTermLength the minimum length of any decomposed term,
     *     any smaller decomposed terms will be discarded. Set to 0 to
     *     indicate no minimum.
     *
     * @deprecated Use {@link #OhAnalyzer(char[], int)} instead
     */
    @Deprecated
    public OhAnalyzer(final CharArraySet punctuationDictionary, final int minimumTermLength) {
        this(punctuationDictionary.toCharArray(), minimumTermLength);
    }

    @Override
    protected TokenStreamComponents createComponents(final String fieldName, final Reader reader) {
        // The WhiteSpace tokenizer doesn't filter anything and also doesn't break on special characters
        final Tokenizer src = new WhitespaceTokenizer(reader);

        /*
         * The OhFilter is really where the work happens.
         * If manages lower-case'ing the terms and also
         * decomposing them based on a list of
         * punctuation term boundaries.
         */
        final TokenStream tok = new OhFilter(src, punctuationDictionary, minimumTermLength);

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
