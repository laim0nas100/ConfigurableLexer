package lt.lb.configurablelexer.token.simple;

import java.util.Objects;
import java.util.function.IntPredicate;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenizer;

/**
 *
 * @author laim0nas100
 */
public class SimpleTokenizer extends BaseTokenizer<SimpleToken> {

    protected IntPredicate isToken;

    @Override
    public boolean isTokenChar(int c) {
        return isToken.test(c);
    }
    
    public SimpleTokenizer(IntPredicate isToken) {
        this.isToken = Objects.requireNonNull(isToken);
    }

    @Override
    public void charListener(boolean isTokenChar, int c) {
        //
    }
    

    @Override
    public ConfTokenBuffer<SimpleToken> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return ConfTokenBuffer.of(new SimpleToken(String.valueOf(buffer, offset, length)));
    }

    @Override
    protected ConfTokenizer<SimpleToken> getMainTokenizer() {
        return this;
    }

}
