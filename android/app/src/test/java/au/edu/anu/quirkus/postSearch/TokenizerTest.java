package au.edu.anu.quirkus.postSearch;

import org.junit.Test;

import static org.junit.Assert.*;

public class TokenizerTest {
    @Test
    public void test() {
        String str = "value sdf #as @30d..9/7 %QUESTION";

        Parser p = new Parser(str);

        p.parse();

//        p.applyConditionsToQuery();

//        Tokenizer tok = new Tokenizer(str);
//
//        Token t = tok.getCurrentToken();
//        while (t != null) {
//
//            tok.next();
//            t = tok.getCurrentToken();
//        }
    }
}
