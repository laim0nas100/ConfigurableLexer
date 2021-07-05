package lt.lb.configurablelexer.token.spec.comment;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.simple.Pos;
import lt.lb.configurablelexer.token.spec.LineAwareCharListener;

/**
 *
 * @author laim0nas100
 */
public abstract class PosAwareCommentCallback<T extends ConfToken> extends PositionAwareCommentCallback<T, Pos> {

    protected LineAwareCharListener lineAware;
    public PosAwareCommentCallback(LineAwareCharListener lineAware,TokenizerCallbacks<T> delegate) {
        super(delegate);
        this.lineAware = lineAware;
    }

    
    
    @Override
    public Pos getPosition() {
        return lineAware.getPos();
    }
}
