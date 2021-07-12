package lt.lb.configurablelexer.token.simple;

import java.util.Objects;
import java.util.function.IntPredicate;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.CharInfo;
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
    
    @Override
    public boolean isBreakChar(int c){
        return false;
    }

    public SimpleTokenizer(IntPredicate isToken) {
        this.isToken = Objects.requireNonNull(isToken);
    }

    @Override
    public void charListener(CharInfo chInfo, int c) {
        //
    }

    @Override
    public ConfTokenBuffer<SimpleToken> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return ConfTokenBuffer.of(new SimpleToken(String.valueOf(buffer, offset, length)));
    }

    @Override
    protected ConfTokenizer<SimpleToken> getCallbacks() {
        return this;
    }

}
