package lt.lb.configurablelexer.token;

import java.io.Reader;
import java.util.List;
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
    public default void charListener(CharInfo chInfo, int c) {
        delegate().charListener(chInfo, c);
    }

    @Override
    public default void produceItems(Consumer<T> consumer) throws Exception {
        delegate().produceItems(consumer);
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
    public default boolean isBreakChar(int c) {
        return delegate().isBreakChar(c);
    }

    @Override
    public default void reset(String string){
        delegate().reset(string);
    }

    @Override
    public default void reset(){
        delegate().reset();
    }

    @Override
    public default boolean isDisabled(){
        return delegate().isDisabled();
    }
    
    

    @Override
    public default boolean readToBuffer() throws Exception {
        return delegate().readToBuffer();
    }

    @Override
    public default T getCurrentBufferedItem() throws Exception {
        return delegate().getCurrentBufferedItem();
    }

    @Override
    public default boolean hasNextBufferedItem() {
        return delegate().hasNextBufferedItem();
    }

    @Override
    public default T getNextBufferedItem() throws Exception {
        return delegate().getNextBufferedItem();
    }

    @Override
    public default ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return delegate().constructTokens(buffer, offset, length);
    }

    @Override
    public default boolean hasCurrentBufferedItem(){
        return delegate().hasCurrentBufferedItem();
    }

    @Override
    public default void close() throws Exception {
        delegate().close();
    }

}
