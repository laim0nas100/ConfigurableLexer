package lt.lb.configurablelexer.token;

import java.io.Reader;
import java.util.function.Consumer;
import lt.lb.configurablelexer.utils.ReusableStringReader;

/**
 *
 * @author laim0nas100
 */
public interface ConfTokenizer<T extends ConfToken> extends AutoCloseable {

    public void reset(Reader input);

    public default void reset(String string) {
        reset(new ReusableStringReader(string));
    }

    public boolean isTokenChar(int c);

    /**
     * You can manually check every char that is being parsed, for example count
     * newlines or global position in file
     *
     * @param isTokenChar
     * @param c
     */
    public void charListener(boolean isTokenChar, int c);

    public boolean readToBuffer() throws Exception;

    public T getCurrentBufferedToken() throws Exception;

    public boolean hasNextBufferedToken();

    public T getNextBufferedToken() throws Exception;

    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception;

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

}
