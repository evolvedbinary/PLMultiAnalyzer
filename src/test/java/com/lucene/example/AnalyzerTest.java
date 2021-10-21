package com.lucene.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AnalyzerTest {


    @Test void LowerCaseWord() throws IOException {
        String SAMPLE_TEXT = "lucene";
        List<String> expected = Arrays.asList("lucene");
        List<String> result = analyze(SAMPLE_TEXT, new WhitespaceAnalyzer());
        Assertions.assertLinesMatch(expected,result);
    }

    @Test void UpperCaseWord() throws IOException {
        String SAMPLE_TEXT = "Lucene";
        List<String> expected = Arrays.asList("Lucene", "lucene");
        List<String> result = analyze(SAMPLE_TEXT, new WhitespaceAnalyzer());
        Assertions.assertLinesMatch(expected,result);
    }

    @Test void PunctuationWord() throws IOException {
        String SAMPLE_TEXT = "banqou's";
        List<String> expected = Arrays.asList("banqou", "banqou's");
        List<String> result = analyze(SAMPLE_TEXT, new WhitespaceAnalyzer());
        Assertions.assertLinesMatch(expected,result);
    }

    @Test void UpperCasePunctuationWord() throws IOException {
        String SAMPLE_TEXT = "Banqou's";
        List<String> expected = Arrays.asList("banqou", "banqou's", "Banqou", "Banqou's");
        List<String> result = analyze(SAMPLE_TEXT, new WhitespaceAnalyzer());
        Assertions.assertLinesMatch(expected,result);
    }

    @Test void AlphanumericWord() throws IOException {
        String SAMPLE_TEXT = "f16";
        List<String> expected = Arrays.asList("f16");
        List<String> result = analyze(SAMPLE_TEXT, new WhitespaceAnalyzer());
        Assertions.assertLinesMatch(expected,result);

    }

    @Test void UpperCaseAlphanumericWord() throws IOException {
        String SAMPLE_TEXT = "F16";
        List<String> expected = Arrays.asList("F16", "f16");
        List<String> result = analyze(SAMPLE_TEXT, new WhitespaceAnalyzer());
        Assertions.assertLinesMatch(expected,result);
    }

    @Test void WhiteSpaceSentence() throws IOException {
        String SAMPLE_TEXT = "lucene is an information retrieval library written in java";
        List<String> expected = Arrays.asList("lucene", "is", "an", "information", "retrieval", "library", "written", "in", "java");
        List<String> result = analyze(SAMPLE_TEXT, new WhitespaceAnalyzer());
        Assertions.assertLinesMatch(expected,result);
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
