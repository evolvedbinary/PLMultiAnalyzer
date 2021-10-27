package com.evolvedbinary.oh;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class OhAnalyzerTest {

    private static String TEXT_FIELD_NAME = "Text";

    @Test
    public void wordSplitTest() throws IOException {
        String s = "banquo's F-16";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer();
        final Analyzer.TokenStreamComponents tokenStreamComponents = ohAnalyzer.createComponents(TEXT_FIELD_NAME, reader);

        final TokenStream tokenStream = tokenStreamComponents.getTokenStream();
        tokenStream.reset();

        System.out.print("\""+s+"\""+" produce: ");

        final List<String> tokens = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            final CharTermAttribute attr = tokenStream.getAttribute(CharTermAttribute.class);
            final String token = attr.toString();
            tokens.add(token);

            System.out.print("["+token+"]");

            //TODO(AR) need to check the other attributes like position and not just the token!
        }

        System.out.println();

        assertArrayEquals(new String[] { "banquo's", "banquo", "16", "f-16" ,"F-16"  }, tokens.toArray(new String[0]));
    }


    @Test
    public void lowerCaseWord() throws IOException {
        String s = "lucene";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer();
        final Analyzer.TokenStreamComponents tokenStreamComponents = ohAnalyzer.createComponents(TEXT_FIELD_NAME, reader);

        final TokenStream tokenStream = tokenStreamComponents.getTokenStream();
        tokenStream.reset();

        System.out.print("\""+s+"\""+" produce: ");

        final List<String> tokens = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            final CharTermAttribute attr = tokenStream.getAttribute(CharTermAttribute.class);
            final String token = attr.toString();
            tokens.add(token);

            System.out.print("["+token+"]");

            //TODO(BH) need to check the other attributes like position and not just the token!
        }
        System.out.println();

        assertArrayEquals(new String[] { "lucene" }, tokens.toArray(new String[0]));
    }

    @Test
    public void UpperCaseWord() throws IOException {
        String s = "Lucene";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer();
        final Analyzer.TokenStreamComponents tokenStreamComponents = ohAnalyzer.createComponents(TEXT_FIELD_NAME, reader);

        final TokenStream tokenStream = tokenStreamComponents.getTokenStream();
        tokenStream.reset();

        System.out.print("\""+s+"\""+" produce: ");

        final List<String> tokens = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            final CharTermAttribute attr = tokenStream.getAttribute(CharTermAttribute.class);
            final String token = attr.toString();
            tokens.add(token);

            System.out.print("["+token+"]");

            //TODO(BH) need to check the other attributes like position and not just the token!
        }
        System.out.println();

        assertArrayEquals(new String[] { "Lucene", "lucene"}, tokens.toArray(new String[0]));
    }

    @Test
    public void PunctuationWord() throws IOException {
        String s = "banqou's";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer();
        final Analyzer.TokenStreamComponents tokenStreamComponents = ohAnalyzer.createComponents(TEXT_FIELD_NAME, reader);

        final TokenStream tokenStream = tokenStreamComponents.getTokenStream();
        tokenStream.reset();

        System.out.print("\""+s+"\""+" produce: ");

        final List<String> tokens = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            final CharTermAttribute attr = tokenStream.getAttribute(CharTermAttribute.class);
            final String token = attr.toString();
            tokens.add(token);

            System.out.print("["+token+"]");

            //TODO(BH) need to check the other attributes like position and not just the token!
        }
        System.out.println();

        assertArrayEquals(new String[] { "banqou's", "banqou"}, tokens.toArray(new String[0]));
    }

    @Test
    public void UpperCasePunctuationWord() throws IOException {
        String s = "Banqou's";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer();
        final Analyzer.TokenStreamComponents tokenStreamComponents = ohAnalyzer.createComponents(TEXT_FIELD_NAME, reader);

        final TokenStream tokenStream = tokenStreamComponents.getTokenStream();
        tokenStream.reset();

        System.out.print("\""+s+"\""+" produce: ");

        final List<String> tokens = new ArrayList<>();
        while (tokenStream.incrementToken()) {
            final CharTermAttribute attr = tokenStream.getAttribute(CharTermAttribute.class);
            final String token = attr.toString();
            tokens.add(token);

            System.out.print("["+token+"]");

            //TODO(BH) need to check the other attributes like position and not just the token!
        }
        System.out.println();

        //TODO(BH) consult Tom with what should be produced here
        assertArrayEquals(new String[] { "Banqou", "banqou's" , "banqou", "Banqou's"}, tokens.toArray(new String[0]));
    }
}
