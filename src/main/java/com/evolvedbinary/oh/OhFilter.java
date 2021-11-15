package com.evolvedbinary.oh;

import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;

// TODO(AR) see CompoundWordTokenFilterBase --- COULD SIMPLIFY THIS?
public final class OhFilter extends TokenFilter {

    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);

    // TODO(AR) this doesn't yet handle extended Unicode punctuation!
    private static final char[] PUNCTUATION_WORD_BOUNDARIES = { '\'', '-', '\u2019' };

    private final ObjectLinkedOpenHashSet<String> extraTerms = new ObjectLinkedOpenHashSet<>(4);
    private State prevInputState;

    public OhFilter(final Tokenizer src) {
        super(src);
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
            return false; // TODO(AR) we have tokens on the stack?
        }

        final CharTermAttribute termAttr = input.getAttribute(CharTermAttribute.class);
        final String term = termAttr.toString();

        // create extra terms by decomposing the term on word boundaries
        decomposePunctuationWordBoundaries(term);

        // if the term starts with an upper-case character, produce a lower-case term
        if (Character.isUpperCase(term.charAt(0))) {
            final String lowerCaseTerm = term.toLowerCase();

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
     * Decomposes a term into multiple additional terms
     * by breaking up the term based on punctuation word boundaries.
     * The additional terms are added to {@link #extraTerms}.
     *
     * @param term the term to decompose
     */
    private void decomposePunctuationWordBoundaries(final String term) {
        for (int i = 0; i < PUNCTUATION_WORD_BOUNDARIES.length; i++) {
            // decompose the term into multiple terms

            // TODO(AR) doesn't handle Unicode yet

            final int idx = term.indexOf(PUNCTUATION_WORD_BOUNDARIES[i]);
            if (idx > -1) {
                // extract the terms before and after the punctuation word boundary
                final String before = term.substring(0, idx);
                final String after = term.substring(idx + 1);

                // ignore terms of 1 character
                if (before.length() > 1) {
                    extraTerms.add(before);
                }

                // ignore terms of 1 character
                if (after.length() > 1) {
                    extraTerms.add(after);
                }
            }
        }
    }
}
