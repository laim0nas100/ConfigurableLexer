package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface DelegatingTokenizerCallbacks<T extends ConfToken> extends TokenizerCallbacks<T> {

    @Override
    public default void reset() {
        delegate().reset();
    }

    @Override
    public default void charListener(boolean isTokenChar, boolean isBreakChar, int c) {
        delegate().charListener(isTokenChar, isBreakChar, c);
    }

    @Override
    public default boolean isTokenChar(int c) {
        return delegate().isTokenChar(c);
    }

    @Override
    public default boolean isBreakChar(boolean isTokenChar, int c) {
        return delegate().isBreakChar(isTokenChar, c);
    }

    @Override
    public default ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return delegate().constructTokens(buffer, offset, length);
    }
    
    
    public TokenizerCallbacks<T> delegate();
    
}
