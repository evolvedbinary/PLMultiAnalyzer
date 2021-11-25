/*
 * The MIT License
 * Copyright © 2021 Evolved Binary
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.evolvedbinary.oh;

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
