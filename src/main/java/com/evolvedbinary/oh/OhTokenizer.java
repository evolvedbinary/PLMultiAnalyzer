package com.evolvedbinary.oh;

import org.apache.lucene.analysis.Tokenizer;

import java.io.IOException;
import java.io.Reader;

public class OhTokenizer extends Tokenizer {
    public OhTokenizer(Reader reader) {
        super(reader);
    }

    @Override
    public boolean incrementToken() throws IOException {
        return false;
    }
}
