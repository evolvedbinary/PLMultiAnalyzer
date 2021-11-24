package com.evolvedbinary.lucene.analyzer;

import org.apache.lucene.analysis.standard.ClassicTokenizer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ClassicTokenizerTest {

    @Test
    public void test1() throws IOException {
        final Reader reader = new StringReader("banquo's F-16");

        final ClassicTokenizer classicTokenizer = new ClassicTokenizer(reader);
        classicTokenizer.reset();

        final List<String> tokens = new ArrayList<>();
        while (classicTokenizer.incrementToken()) {
            final CharTermAttribute attr = classicTokenizer.getAttribute(CharTermAttribute.class);
            final String token = attr.toString();
            tokens.add(token);

            System.out.println(token);

            //TODO(AR) need to check the other attributes like position and not just the token!
        }

        assertArrayEquals(new String[] { "banquo's", "F-16"  }, tokens.toArray(new String[0]));
    }

}
