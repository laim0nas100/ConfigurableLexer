package lt.lb.configurablelexer.token.spec.string;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * Up to user to determine what to do if parsed char count is not one.
 *
 * @author laim0nas100
 */
public abstract class CharAwareCallback<T extends ConfToken, PosInfo> extends StringAwareCallbackBase<T, PosInfo> {

    public CharAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
        this.startPred = c -> c == '\'';
        this.endPred = c -> c == '\'';
    }

    public CharAwareCallback() {
        this.startPred = c -> c == '\'';
        this.endPred = c -> c == '\'';
    }

}
