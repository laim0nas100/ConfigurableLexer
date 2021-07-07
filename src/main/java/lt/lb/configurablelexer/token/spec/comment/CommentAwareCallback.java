package lt.lb.configurablelexer.token.spec.comment;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.spec.ExtendedPositionAwareSplittableCallbackBase;

/**
 *
 * @author laim0nas100
 * @param <T>
 * @param <PosInfo>
 */
public abstract class CommentAwareCallback<T extends ConfToken, PosInfo> extends ExtendedPositionAwareSplittableCallbackBase<T,PosInfo> {

    public CommentAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public CommentAwareCallback() {
        super();
    }
}
