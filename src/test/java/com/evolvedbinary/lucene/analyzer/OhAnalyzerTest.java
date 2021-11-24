package com.evolvedbinary.lucene.analyzer;

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

    private static final char[] PUNCTUATION_DICTIONARY = { '\'', '-', '\u2019' };
    private static final int MINIMUM_TERM_LENGTH = 2;

    private static String TEXT_FIELD_NAME = "Text";

    @Test
    public void wordSplitTest() throws IOException {
        String s = "banquo's F-16";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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

        assertArrayEquals(new String[] { "banquo's", "banquo", "F-16", "16", "f-16"  }, tokens.toArray(new String[0]));
    }


    @Test
    public void lowerCaseWord() throws IOException {
        String s = "lucene";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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
    public void nameCaseWord() throws IOException {
        String s = "Lucene";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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

        assertArrayEquals(new String[] { "Lucene", "lucene" }, tokens.toArray(new String[0]));
    }

    @Test
    public void upperCaseWord() throws IOException {
        String s = "LUCENE";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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

        assertArrayEquals(new String[] { "LUCENE", "lucene"}, tokens.toArray(new String[0]));
    }

    @Test
    public void PunctuationWord() throws IOException {
        String s = "banqou's";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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
        assertArrayEquals(new String[] { "Banqou's", "Banqou", "banqou's" , "banqou"}, tokens.toArray(new String[0]));
    }

    // TS,ts,(TS)
    @Test
    public void brackets() throws IOException {
        String s = "(TS)";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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
        assertArrayEquals(new String[] { "(TS)", "TS", "ts"}, tokens.toArray(new String[0]));
    }

    @Test
    public void forwardSlash() throws IOException {
        String s = "S/S";
        final Reader reader = new StringReader(s);

        final OhAnalyzer ohAnalyzer = new OhAnalyzer(PUNCTUATION_DICTIONARY, MINIMUM_TERM_LENGTH);
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
