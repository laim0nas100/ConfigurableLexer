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
    public default void charListener(CharInfo chInfo, int c) {
        for(CharListener listener:listeners()){
            listener.charListener(chInfo, c);
        }
    }
    
}
