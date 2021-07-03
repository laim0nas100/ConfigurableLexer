package lt.lb.configurablelexer.token.simple;

import java.io.Reader;
import java.util.Objects;
import java.util.function.IntPredicate;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.LineAwareCharListener;

/**
 *
 * @author laim0nas100
 */
public class SimplePosTokenizer extends BaseTokenizer<SimplePosToken> {

    protected IntPredicate isToken;
    protected LineAwareCharListener lineAware;

    @Override
    public boolean isTokenChar(int c) {
        return isToken.test(c);
    }

    @Override
    public void reset(Reader input) {
        super.reset(input);
        lineAware.reset();// add all listeners  with reset
    }

    @Override
    public void charListener(boolean isTokenChar, int c) {
        lineAware.listen(isTokenChar, c);
        // must provide some implementation to prevent infinite recursion
    }

    public SimplePosTokenizer(IntPredicate isToken) {
        this.isToken = Objects.requireNonNull(isToken);
        lineAware = new LineAwareCharListener();

    }

    @Override
    public ConfTokenBuffer<SimplePosToken> constructTokens(char[] buffer, int offset, int length) throws Exception {
        String value = String.valueOf(buffer, offset, length);
        Pos pos = new Pos(lineAware.getLine() + 1, lineAware.getColumn() - length);
        return ConfTokenBuffer.of(new SimplePosToken(pos, value));
    }

    @Override
    protected ConfTokenizer<SimplePosToken> getCallbacks() {
        return this;
    }

}
