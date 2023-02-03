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
package com.evolvedbinary.lucene.analyzer;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

// TODO(AR) see CompoundWordTokenFilterBase --- COULD SIMPLIFY THIS?
public final class OhFilter extends TokenFilter {

    private final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
//    private final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);

    private final char[] punctuationDictionary;
    private final int minimumTermLength;

    private final ObjectLinkedOpenHashSet<String> extraTerms = new ObjectLinkedOpenHashSet<>(4);
    private State prevInputState;

    /**
     * @param src the tokenizer
     * @param punctuationDictionary the dictionary of punctuation to use
     *     for decomposition.
     * @param minimumTermLength the minimum length of any decomposed term,
     *     any smaller decomposed terms will be discarded. Set to 0 to
     *     indicate no minimum.
     */
    public OhFilter(final Tokenizer src, final char[] punctuationDictionary, final int minimumTermLength) {
        super(src);
        this.punctuationDictionary = punctuationDictionary;
        this.minimumTermLength = minimumTermLength;
    }

    @Override
    public boolean incrementToken() throws IOException {

        // do we have extra terms that we need to output
        if (!extraTerms.isEmpty()) {
            // output the first extra term
            final String extraTerm = extraTerms.removeFirst();
            termAtt.setEmpty().append(extraTerm);

            /*
                The PositionIncrementAttribute needs to be set to 0 on
                subsequent decomposed terms, to indicate that it is a
                stem of the original term.
                See {@link org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute}
             */
            posIncAtt.setPositionIncrement(0);

            // TODO(AR) does this need updating too?
            //offsetAtt.setOffset(token.startOffset, token.endOffset);

            return true;
        }

        // do we have a previous state to restore
        if (prevInputState != null) {
            input.restoreState(prevInputState);
            prevInputState = null;
        }

        // can we get the next token from the source?
        if (!input.incrementToken()) {
            return false;
        }

        CharTermAttribute termAttr = input.getAttribute(CharTermAttribute.class);
        final String term = termAttr.toString();

        //if the term is wrapped by backticks
        // remove the backticks for the token
        if(wrapedByBackticks(term)) {
            termAttr.setEmpty();
            termAttr.append(term.substring(1,term.length() -1));
        }

        // create extra terms by decomposing the term on word boundaries
        decomposePunctuationWordBoundaries(term);

        final String lowerCaseTerm = term.toLowerCase();

        // decide whether we lowercase the term or not
        // the decision will be based on if the term has a lowerCase char in it
        // if the token is wrapped in backticks we will skip this step
        if (!term.equals(lowerCaseTerm) && !wrapedByBackticks(term)) {

            // add the lower-case term as an extra term
            extraTerms.add(lowerCaseTerm);

            // create extra terms by decomposing the lowerCaseTerm on word boundaries
            decomposePunctuationWordBoundaries(lowerCaseTerm);
        }

        if (!extraTerms.isEmpty()) {
            /*
                As we found some extra terms that we will output,
                on our next entry into this loop, record the current state,
                so that we can restore it after we have output each extra terms
             */
            this.prevInputState = input.captureState();
        }

        /*
            The PositionIncrementAttribute needs to be set to 1 on
            the first/origin term.
            See {@link org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute}
         */
        posIncAtt.setPositionIncrement(1);

        // TODO(AR) does this need updating too?
        //offsetAtt.setOffset(token.startOffset, token.endOffset);

        return true;
    }

    /**
     * @param term
     * @return whether or not the term is wrapped by double backticks
     * this is used in exact term matching
     */
    private boolean wrapedByBackticks(String term) {
        return term.charAt(0) == '`' && term.charAt(0) == term.charAt(term.length() -1);
    }

    /**
     * Decomposes a term into multiple additional terms
     * by breaking up the term based on punctuation word boundaries.
     * The additional terms are added to {@link #extraTerms}.
     *
     * @param term the term to decompose
     */
    private void decomposePunctuationWordBoundaries(final String term) {
        for (int i = 0; i < punctuationDictionary.length; i++) {
            // decompose the term into multiple terms

            // TODO(AR) doesn't handle Unicode yet

            final int idx = term.indexOf(punctuationDictionary[i]);
            if (idx > -1) {
                // extract the terms before and after the punctuation word boundary
                final String before = term.substring(0, idx);
                final String after = term.substring(idx + 1);

                // ignore `before` terms that are shorter than minimumTermLength
                if (before.length() >= minimumTermLength) {
                    extraTerms.add(before);
                }

                // ignore `after` terms that are shorter than minimumTermLength
                if (after.length() >= minimumTermLength) {
                    extraTerms.add(after);
                }
            }
        }
    }
}
