package lt.lb.configurablelexer.token.spec.string;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public abstract class StringAwareCallback<T extends ConfToken, PosInfo> extends StringAwareCallbackBase<T,PosInfo> {

    public StringAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public StringAwareCallback() {
    }
}
