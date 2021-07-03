package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface TokenizerCallbacksListeners<T extends ConfToken> extends TokenizerCallbacks<T> {

    public Iterable<CharListener> listeners();
    
    @Override
    public default void reset() {
        for(CharListener listener:listeners()){
            listener.reset();
        }
    }

    @Override
    public default void charListener(boolean isTokenChar, int c) {
        for(CharListener listener:listeners()){
            listener.listen(isTokenChar, c);
        }
    }
    
}
