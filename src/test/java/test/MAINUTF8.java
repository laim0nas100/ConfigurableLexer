package test;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.simple.SimplePosTokenizer;

/**
 *
 * @author laim0nas100
 */
public class MAINUTF8 {

    public static final boolean DEBUG = false;

    public static void main(String[] args) throws Exception {

        String term = "\nok1 ok2\n *hell?o??" + " NOT  **something else?* regular";

        term += "你好, おはよう, α-Ω\uD834\uDD1E";
        
        System.out.println(term.length()+" codepoints "+term.codePoints().count());

        SimplePosTokenizer simplePosTokenizer = new SimplePosTokenizer(c -> !Character.isWhitespace(c));
        ConfTokenizer myTokenizer = simplePosTokenizer;
        myTokenizer.reset(term);

        try (PrintWriter writer = new PrintWriter(new File("out.txt"), "UTF-8")) {
            myTokenizer.produceItems(t -> {
                writer.println(t);
            });
        }

    }

}
