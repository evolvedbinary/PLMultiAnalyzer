package com.lucene.example;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;

import java.io.IOException;
import java.util.Locale;

public final class MyStopWordFilter extends TokenFilter {
    private CharTermAttribute charTermAtt;
    private PositionIncrementAttribute posIncrAtt;
    public MyStopWordFilter(TokenStream input) {
        super(input);
        charTermAtt = addAttribute(CharTermAttribute.class);
        posIncrAtt = addAttribute(PositionIncrementAttribute.class);
    }

    //choosing what to filter
    //[choosing] [what] [to] [filter]
    //[what]
    @Override
    public boolean incrementToken() throws IOException {
        int extraIncrement = 0;
        boolean returnValue = false;
        while (input.incrementToken()) {
            System.out.println("term: "+(charTermAtt.toString()));

            if (StopAnalyzer.ENGLISH_STOP_WORDS_SET.contains
                    (charTermAtt.toString().toLowerCase())) {
                extraIncrement++;// filter this word
                continue;
            }
            returnValue = true;
            break;
        }
        if(extraIncrement>0){
            posIncrAtt.setPositionIncrement
                    (posIncrAtt.getPositionIncrement()+extraIncrement);
        }
        return returnValue;
    }
}