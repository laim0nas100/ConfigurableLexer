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
        public ConfTokenizer<T> getDelegate() {
            return delegate;
        }

    }

    public ConfTokenizer<T> getDelegate();

    @Override
    public default void charListener(boolean isTokenChar, int c) {
        getDelegate().charListener(isTokenChar, c);
    }

    @Override
    public default void produceTokens(Consumer<T> consumer) throws Exception {
        getDelegate().produceTokens(consumer);
    }
    
    
    
    

    @Override
    public default void reset(Reader input) {
        getDelegate().reset(input);
    }

    @Override
    public default boolean isTokenChar(int c) {
        return getDelegate().isTokenChar(c);
    }

    @Override
    public default boolean readToBuffer() throws Exception {
        return getDelegate().readToBuffer();
    }

    @Override
    public default T getCurrentBufferedToken() throws Exception {
        return getDelegate().getCurrentBufferedToken();
    }

    @Override
    public default boolean hasNextBufferedToken() {
        return getDelegate().hasNextBufferedToken();
    }

    @Override
    public default T getNextBufferedToken() throws Exception {
        return getDelegate().getNextBufferedToken();
    }

    @Override
    public default ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return getDelegate().constructTokens(buffer, offset, length);
    }

    @Override
    public default void close() throws Exception {
        getDelegate().close();
    }

}
