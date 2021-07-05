package lt.lb.configurablelexer.token;

import java.io.Reader;
import java.util.Objects;
import java.util.function.Consumer;

/**
 *
 * @author laim0nas100
 */
public interface DelegatingConfTokenizer<T extends ConfToken> extends ConfTokenizer<T> {

    public static class DelegatingTokenizerBase<T extends ConfToken> implements DelegatingConfTokenizer<T> {

        protected ConfTokenizer<T> delegate;

        public DelegatingTokenizerBase(ConfTokenizer<T> main) {
            this.delegate = Objects.requireNonNull(main);
        }

        @Override
        public ConfTokenizer<T> delegate() {
            return delegate;
        }

    }

    public ConfTokenizer<T> delegate();

    @Override
    public default void charListener(boolean isTokenChar,boolean isBreakChar, int c) {
        delegate().charListener(isTokenChar,isBreakChar, c);
    }

    @Override
    public default void produceTokens(Consumer<T> consumer) throws Exception {
        delegate().produceTokens(consumer);
    }
    
    @Override
    public default void reset(Reader input) {
        delegate().reset(input);
    }

    @Override
    public default boolean isTokenChar(int c) {
        return delegate().isTokenChar(c);
    }

    @Override
    public default boolean readToBuffer() throws Exception {
        return delegate().readToBuffer();
    }

    @Override
    public default T getCurrentBufferedToken() throws Exception {
        return delegate().getCurrentBufferedToken();
    }

    @Override
    public default boolean hasNextBufferedToken() {
        return delegate().hasNextBufferedToken();
    }

    @Override
    public default T getNextBufferedToken() throws Exception {
        return delegate().getNextBufferedToken();
    }

    @Override
    public default ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return delegate().constructTokens(buffer, offset, length);
    }

    @Override
    public default boolean hasCurrentBufferedToken() throws Exception {
        return delegate().hasCurrentBufferedToken();
    }

    @Override
    public default void close() throws Exception {
        delegate().close();
    }

}
