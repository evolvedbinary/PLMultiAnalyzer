import org.apache.lucene.analysis.util.CharTokenizer;

import java.io.Reader;


public class MyTokenizer extends CharTokenizer {

    public MyTokenizer(Reader input) {
        super(input);
    }

    //choosing what to use as a separator
    @Override
    protected boolean isTokenChar(int i) {
        return !Character.isSpaceChar(i);
    }
    // "Lucene is an Information Retrieval library written in Java."
    //        -  -  -           -         -       -       -  -
    //[Lucene][is][an][information]
}
