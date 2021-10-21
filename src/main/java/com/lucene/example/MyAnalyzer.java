package com.lucene.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import java.io.Reader;

public class MyAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String s, Reader reader) {
        Tokenizer letterTokenizer = new MyTokenizer(reader); // split the string into terms based on what you want
        // [lucene] [lucnen]
        TokenStream filter = new MyStopWordFilter(letterTokenizer);
//        TokenStream secondStep = Operation(filter);
        return new TokenStreamComponents(letterTokenizer, filter);
    }

}