package com.evolvedbinary.oh;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class StandardTokenizerTest {

    @Test
    public void test1() throws IOException {
        final Reader reader = new StringReader("banquo's F-16");

        final StandardTokenizer standardTokenizer = new StandardTokenizer(reader);
        standardTokenizer.reset();

        final List<String> tokens = new ArrayList<>();
        while (standardTokenizer.incrementToken()) {
            final CharTermAttribute attr = standardTokenizer.getAttribute(CharTermAttribute.class);
            final String token = attr.toString();
            tokens.add(token);

            System.out.println(token);

            //TODO(AR) need to check the other attributes like position and not just the token!
        }

        assertArrayEquals(new String[] { "banquo", "banquo's", "16", "f-16"  }, tokens.toArray(new String[0]));
    }
}
