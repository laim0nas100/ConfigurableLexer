package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface StatefulTokenizerCallbacks<T extends ConfToken, State> extends DelegatingTokenizerCallbacks<T> {

    public void setState(State newState);

    public State getState();

    public TokenizerCallbacks<T> getCallbacks(State state);

    @Override
    public default TokenizerCallbacks<T> delegate() {
        return getCallbacks(getState());
    }

}
