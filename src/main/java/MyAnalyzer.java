import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;

import java.io.Reader;

public class MyAnalyzer extends Analyzer {
    @Override
    protected TokenStreamComponents createComponents(String s, Reader reader) {
        Tokenizer letterTokenizer = new MyTokenizer(reader);
        TokenStream filter = new MyStopWordFilter(letterTokenizer);
        return new TokenStreamComponents(letterTokenizer,filter);
    }
}
