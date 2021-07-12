package test;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.simple.Pos;
import lt.lb.configurablelexer.token.simple.SimplePosToken;
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

        SimplePosTokenizer simplePosTokenizer = new SimplePosTokenizer(c -> !Character.isWhitespace(c));
        List list = new ArrayList<>();
        ConfTokenizer myTokenizer = simplePosTokenizer;
        myTokenizer.reset(term);
        myTokenizer.produceItems(t -> {
            list.add(t);
        });
        PrintWriter writer = new PrintWriter(new File("out.txt"), "UTF-8");
        for (Object ttt : list) {

            writer.println(ttt);
        }

        writer.close();

    }

}
