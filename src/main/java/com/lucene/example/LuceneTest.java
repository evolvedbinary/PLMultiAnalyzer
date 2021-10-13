package com.lucene.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LuceneTest {
    private static final String SAMPLE_TEXT
            = "This is baeldung.com Lucene Analyzers test";
    public static void main(String[] args) throws IOException {
        List<String> result = analyze(SAMPLE_TEXT, new MyAnalyzer());
        System.out.println(result);
    }
    public static List<String> analyze(String text, Analyzer analyzer) throws IOException {
        List<String> result = new ArrayList<String>();
        TokenStream tokenStream = analyzer.tokenStream("FIELD_NAME", text);
        CharTermAttribute attr = tokenStream.addAttribute(CharTermAttribute.class);
        tokenStream.reset();
        while(tokenStream.incrementToken()) {
            result.add(attr.toString());
        }
        return result;
    }
}
