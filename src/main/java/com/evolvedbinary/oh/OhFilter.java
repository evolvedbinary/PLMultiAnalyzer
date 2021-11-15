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

        // do we have extra terms waiting to output
        if (!extraTerms.isEmpty()) {
            // output the first extra term
            final String extraTerm = extraTerms.removeFirst();
            termAtt.setEmpty().append(extraTerm);

            // TODO(AR) do these need updating too?
            //offsetAtt.setOffset(token.startOffset, token.endOffset);

            // TODO(AR) experimental to try cause use of OR vs AND
//            posIncAtt.setPositionIncrement(0);

            return true;
        }

        // do we have a previous state to restore
        if (prevInputState != null) {
            input.restoreState(prevInputState);
            prevInputState = null;
            return true;
        }

        // can we get the next token from the source?
        if (!input.incrementToken()) {
            return false; // TODO(AR) we have tokens on the stack?
        }
//        this doesnt work with WhitespaceTokenizer
//        final TypeAttribute typeAttr = input.getAttribute(TypeAttribute.class);
//        if (StandardTokenizer.TOKEN_TYPES[StandardTokenizer.ALPHANUM].equals(typeAttr.type())) {
        // <ALPHANUM>

        final CharTermAttribute termAttr = input.getAttribute(CharTermAttribute.class);
        final String term = termAttr.toString();


        for (int i = 0; i < PUNCTUATION_WORD_BOUNDARIES.length; i++) {
            // decompose the term into multiple tokens

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


        // produce a lowerCase term

        if (Character.isUpperCase(term.charAt(0))) {

            final String lowerCaseTerm = term.toLowerCase();

            extraTerms.add(lowerCaseTerm);

            for (int i = 0; i < PUNCTUATION_WORD_BOUNDARIES.length; i++) {
                // decompose the term into multiple tokens

                // TODO(AR) doesn't handle Unicode yet

                final int idx = lowerCaseTerm.indexOf(PUNCTUATION_WORD_BOUNDARIES[i]);
                if (idx > -1) {
                    // extract the terms before and after the punctuation word boundary
                    final String before = lowerCaseTerm.substring(0, idx);
                    final String after = lowerCaseTerm.substring(idx + 1);

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
//        }

        if (!extraTerms.isEmpty()) {
            // we found some extra terms we need to produce

            // record the current state, so we can restore it later
            this.prevInputState = input.captureState();

            // output the first extra term
            final String extraTerm = extraTerms.removeFirst();
            termAtt.setEmpty().append(extraTerm);
            // TODO(AR) these need updating too!
            //offsetAtt.setOffset(token.startOffset, token.endOffset);
            //posIncAtt.setPositionIncrement(0);

            return true;
        }

        return true;
    }
}

//TODO(BH) make this into an actual app thats usable
