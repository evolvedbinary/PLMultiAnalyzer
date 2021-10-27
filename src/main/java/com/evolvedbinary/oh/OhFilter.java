package com.evolvedbinary.oh;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TypeAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO(AR) see CompoundWordTokenFilterBase --- COULD SIMPLIFY THIS?
public final class OhFilter extends TokenFilter {

    protected final CharTermAttribute termAtt = addAttribute(CharTermAttribute.class);
    protected final OffsetAttribute offsetAtt = addAttribute(OffsetAttribute.class);
    private final PositionIncrementAttribute posIncAtt = addAttribute(PositionIncrementAttribute.class);

    // TODO(AR) this doesn't yet handle extended Unicode punctuation!
    private static final char[] PUNCTUATION_WORD_BOUNDARIES = { '\'','-' };

    private List<String> extraWords = null;
    private State prevInputState;

    public OhFilter(final Tokenizer src) {
        super(src);
    }

    @Override
    public boolean incrementToken() throws IOException {

        // do we have tokens waiting to output
        if (extraWords != null) {
            // output the first extra word
            final String extraWord = extraWords.remove(0);
            termAtt.setEmpty().append(extraWord);
            // TODO(AR) these need updating too!
            //offsetAtt.setOffset(token.startOffset, token.endOffset);
            //posIncAtt.setPositionIncrement(0);

            // clear memory if not needed
            if (extraWords.isEmpty()) {
                extraWords = null;
            }

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
                // decompose the token into multiple tokens

                // TODO(AR) doesn't handle Unicode yet

                final int idx = term.indexOf(PUNCTUATION_WORD_BOUNDARIES[i]);
                if (idx > -1) {
                    // extract the words before and after the punctuation mark
                    final String before = term.substring(0, idx);
                    final String after = term.substring(idx + 1);

                    // ignore words of 1 character
                    if (before.length() > 1) {
                        if (this.extraWords == null) {
                            this.extraWords = new ArrayList<>(1);
                        }
                        this.extraWords.add(before);
                    }

                    // ignore words of 1 character
                    if (after.length() > 1) {
                        if (this.extraWords == null) {
                            this.extraWords = new ArrayList<>(1);
                        }
                        this.extraWords.add(after);
                    }
                }
            }



        //produce a lowerCase token

        if(Character.isUpperCase(term.charAt(0))) {
            if (this.extraWords == null) {
                this.extraWords = new ArrayList<>(1);
            }
            this.extraWords.add(term.toLowerCase());

            String lowerCaseWord = term.toLowerCase();

            for (int i = 0; i < PUNCTUATION_WORD_BOUNDARIES.length; i++) {
                // decompose the token into multiple tokens

                // TODO(AR) doesn't handle Unicode yet

                final int idx = lowerCaseWord.indexOf(PUNCTUATION_WORD_BOUNDARIES[i]);
                if (idx > -1) {
                    // extract the words before and after the punctuation mark
                    final String before = lowerCaseWord.substring(0, idx);
                    final String after = lowerCaseWord.substring(idx + 1);

                    // ignore words of 1 character
                    if (before.length() > 1) {
                        if (this.extraWords == null) {
                            this.extraWords = new ArrayList<>(1);
                        }
                        this.extraWords.add(before);
                    }

            if (this.extraWords != null) {
                // we found some extra words we need to produce
                this.prevInputState = input.captureState();


                // output the first extra word
                final String extraWord = extraWords.remove(0);
                termAtt.setEmpty().append(extraWord);
                // TODO(AR) these need updating too!
                //offsetAtt.setOffset(token.startOffset, token.endOffset);
                //posIncAtt.setPositionIncrement(0);

                // clear memory if not needed
                if (extraWords.isEmpty()) {
                    extraWords = null;
                }

                return true;
            }



                    // ignore words of 1 character
                    if (after.length() > 1) {
                        if (this.extraWords == null) {
                            this.extraWords = new ArrayList<>(1);
                        }
                        this.extraWords.add(after);
                    }
                }
            }

        }
//        }

        return true;
    }
}
