package lt.lb.configurablelexer.token.spec;

import java.util.HashMap;
import java.util.Map;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.StatefulTokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public class MapStatefullCallbacks<T extends ConfToken,State> implements StatefulTokenizerCallbacks<T, State> {

    protected State currState;
    protected Map<State,TokenizerCallbacks<T>> callbacks = new HashMap<>();
    
    
    @Override
    public void setState(State newState) {
        currState = newState;
    }

    @Override
    public State getState() {
        return currState;
    }

    @Override
    public TokenizerCallbacks<T> getCallbacks(State state) {
        return callbacks.get(state);
    }
    
}
