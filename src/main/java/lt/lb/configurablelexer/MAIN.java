package lt.lb.configurablelexer;

import java.io.File;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import lt.lb.commons.DLog;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.simple.Pos;
import lt.lb.configurablelexer.token.simple.SimplePosToken;
import lt.lb.configurablelexer.token.simple.SimplePosTokenizer;
import lt.lb.configurablelexer.token.simple.SimpleTokenizer;

/**
 *
 * @author laim0nas100
 */
public class MAIN {
    public static final boolean DEBUG = false;

    public static void main(String[] args) throws Exception {

        String term = "\nok1 ok2\n *hell?o?? *help?me?jesus?\n" + "  NOT  **something else?* regular";

        term += "你好, おはよう, α-Ω\uD834\uDD1E";

//        term = term + term + term;
//        term = term + term;

        BaseTokenizer<SimplePosToken> tokenizer_base = new BaseTokenizer<SimplePosToken>() {
            @Override
            public void reset(Reader input) {
                super.reset(input);
                line = 0;
                col = 0;
                lastToken = -1;
            }

            int line = 0;
            int col = 0;
            int lastToken = -1;

            @Override
            public boolean isTokenChar(int c) {
                return !Character.isWhitespace(c);
            }

            @Override
            public void charListener(CharInfo chInfo, int c) {
                if (lastToken == '\n') {// assuming we break on new line, so one token later
                    line++;
                    col = 0;
                }
                lastToken = c;
                col++;

            }

            @Override
            public ConfTokenBuffer<SimplePosToken> constructTokens(char[] buffer, int offset, int length) throws Exception {
                ArrayList<SimplePosToken> tokens = new ArrayList<>();
                String string = String.valueOf(buffer, offset, length);

                SimplePosToken simpleToken = new SimplePosToken(new Pos(line + 1, col - length), string);
                tokens.add(simpleToken);
                return ConfTokenBuffer.ofList(tokens);
            }

            @Override
            protected ConfTokenizer<SimplePosToken> getCallbacks() {
                return this;
            }

        };

        SimplePosTokenizer simplePosTokenizer = new SimplePosTokenizer(c -> !Character.isWhitespace(c));
        SimpleTokenizer simpleTokenizer = new SimpleTokenizer(c -> !Character.isWhitespace(c));
        List list = new ArrayList<>();
        ConfTokenizer myTokenizer = simplePosTokenizer;
        myTokenizer.reset(term);
        myTokenizer.produceItems(t->{
            list.add(t);
            DLog.print(t);
        });
        PrintWriter writer = new PrintWriter(new File("out.txt"), "UTF-8");
        for (Object ttt : list) {
            
            writer.println(ttt);
        }

        writer.close();

        DLog.print("hi");
        DLog.print(term);
    }

}
