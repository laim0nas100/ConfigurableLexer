package lt.lb.configurablelexer.lexer;

import java.io.Reader;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenizer;

/**
 *
 * @author laim0nas100
 */
public abstract class MappingLexer<T extends ConfToken, O extends ConfToken> implements ConfTokenizer<T> {

    protected int currentTokenIndex;
    protected ConfTokenBuffer<T> bufferedTokens;

    public abstract ConfTokenBuffer<T> map(ConfTokenBuffer<O> old);

    public abstract ConfTokenizer<O> getOriginal();

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return map(getOriginal().constructTokens(buffer, offset, length));
    }

    @Override
    public void reset(Reader input) {
        getOriginal().reset(input);
        currentTokenIndex = 0;
        bufferedTokens = ConfTokenBuffer.of();
    }

    @Override
    public boolean isTokenChar(int c) {
        return getOriginal().isTokenChar(c);
    }

    @Override
    public void charListener(CharInfo chInfo, int c) {
        getOriginal().charListener(chInfo, c);
    }

    @Override
    public boolean readToBuffer() throws Exception {
        return getOriginal().readToBuffer();
    }

    @Override
    public T getCurrentBufferedItem() throws Exception {
        return this.bufferedTokens.get(this.currentTokenIndex);
    }

    @Override
    public boolean hasNextBufferedItem() {
        return this.bufferedTokens.size() > this.currentTokenIndex + 1;
    }

    @Override
    public T getNextBufferedItem() throws Exception {
        if (this.bufferedTokens.size() > this.currentTokenIndex + 1) {
            this.currentTokenIndex++;
            return getCurrentBufferedItem();
        }
        throw new IllegalStateException("Need to read from input, no more buffered tokens left");
    }

    @Override
    public void close() throws Exception {
        getOriginal().close();
    }

}
