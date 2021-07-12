package lt.lb.configurablelexer.token;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import lt.lb.configurablelexer.utils.BufferedIterator;
import lt.lb.configurablelexer.utils.ReusableStringReader;

/**
 *
 * @author laim0nas100
 */
public interface ConfTokenizer<T extends ConfToken> extends AutoCloseable, TokenizerCallbacks<T>, BufferedIterator<T> {

    public void reset(Reader input);

    public default void reset(String string) {
        reset(new ReusableStringReader(string));
    }

    @Override
    public boolean readToBuffer() throws Exception;

    @Override
    public boolean hasCurrentBufferedItem();

    @Override
    public T getCurrentBufferedItem() throws Exception;

    @Override
    public boolean hasNextBufferedItem();

    @Override
    public T getNextBufferedItem() throws Exception;

}
