package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface TokenizerCallbacks<T extends ConfToken> extends ConfTokenConstructor<T> {

    public default void reset(){
        
    }

    public void charListener(boolean isTokenChar, int c);

    public boolean isTokenChar(int c);
    
    
}
