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
    public default void charListener(CharInfo chInfo, int c) {
        delegate().charListener(chInfo, c);
    }

    @Override
    public default boolean isTokenChar(int c) {
        return delegate().isTokenChar(c);
    }

    @Override
    public default boolean isBreakChar(int c) {
        return delegate().isBreakChar(c);
    }

    @Override
    public default ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return delegate().constructTokens(buffer, offset, length);
    }

    @Override
    public default boolean isDisabled() {
        return delegate().isDisabled();
    }

    public TokenizerCallbacks<T> delegate();

}
