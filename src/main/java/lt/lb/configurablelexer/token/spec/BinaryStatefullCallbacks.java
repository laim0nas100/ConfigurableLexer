package lt.lb.configurablelexer.token.spec;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.StatefulTokenizerCallbacks;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public class BinaryStatefullCallbacks<T extends ConfToken> implements StatefulTokenizerCallbacks<T, Boolean> {

    protected TokenizerCallbacks<T> whenTrue;
    protected TokenizerCallbacks<T> whenFalse;
    protected boolean state;

    public BinaryStatefullCallbacks(TokenizerCallbacks<T> whenTrue, TokenizerCallbacks<T> whenFalse) {
    }

    public BinaryStatefullCallbacks() {
    }

    public TokenizerCallbacks<T> getWhenTrue() {
        return whenTrue;
    }

    public void setWhenTrue(TokenizerCallbacks<T> whenTrue) {
        this.whenTrue = whenTrue;
    }

    public TokenizerCallbacks<T> getWhenFalse() {
        return whenFalse;
    }

    public void setWhenFalse(TokenizerCallbacks<T> whenFalse) {
        this.whenFalse = whenFalse;
    }

    @Override
    public void setState(Boolean newState) {
        this.state = newState;
    }

    @Override
    public Boolean getState() {
        return this.state;
    }

    @Override
    public TokenizerCallbacks<T> getCallbacks(Boolean state) {
        return state ? whenTrue : whenFalse;
    }

}
