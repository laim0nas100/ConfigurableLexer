package lt.lb.configurablelexer.token;

import java.io.Reader;

/**
 *
 * @author laim0nas100
 */
public abstract class BufferedConfTokenizer<T extends ConfToken> implements ConfTokenizer<T> {

    protected ConfTokenBuffer<T> bufferedTokens = ConfTokenBuffer.of();
    protected int currentTokenIndex;
    
    @Override
    public void reset(Reader input) {
         bufferedTokens = ConfTokenBuffer.of();
         currentTokenIndex = 0;
         reset();
    }

    @Override
    public boolean hasCurrentBufferedItem() {
        return bufferedTokens.size() > currentTokenIndex;
    }

    @Override
    public T getCurrentBufferedItem() throws Exception {
        return bufferedTokens.get(currentTokenIndex);
    }

    @Override
    public boolean hasNextBufferedItem() {
        return bufferedTokens.size() > currentTokenIndex +1;
    }

    @Override
    public T getNextBufferedItem() throws Exception {
        if (this.bufferedTokens.size() > this.currentTokenIndex + 1) {
            this.currentTokenIndex++;
            return getCurrentBufferedItem();
        }
        throw new IllegalStateException("Need to read from input, no more buffered tokens left");
    }
    
}
