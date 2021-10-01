import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;

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

    //TODO write a test given a string return the terms => assert to be equal
    //TODO given a LIST of Token apply analyzer to them => assert contains

}