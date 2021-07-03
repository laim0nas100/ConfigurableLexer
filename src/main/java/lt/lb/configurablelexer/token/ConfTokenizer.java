package lt.lb.configurablelexer.token;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lt.lb.configurablelexer.utils.ReusableStringReader;

/**
 *
 * @author laim0nas100
 */
public interface ConfTokenizer<T extends ConfToken> extends AutoCloseable, TokenizerCallbacks<T> {

    public void reset(Reader input);

    public default void reset(String string) {
        reset(new ReusableStringReader(string));
    }

    public boolean readToBuffer() throws Exception;

    public T getCurrentBufferedToken() throws Exception;

    public boolean hasNextBufferedToken();

    public T getNextBufferedToken() throws Exception;

    public default void produceTokens(Consumer<T> consumer) throws Exception {
        while (true) {
            if (!hasNextBufferedToken()) {
                boolean read = readToBuffer();
                if (!read) {
                    break;
                } else {
                    T t = getCurrentBufferedToken();
                    consumer.accept(t);
                }
            } else {
                T t = getNextBufferedToken();
                consumer.accept(t);
            }
        }

    }

    public default List<T> produceTokens() throws Exception {
        ArrayList<T> list = new ArrayList<>();
        produceTokens(list::add);
        return list;
    }

}
