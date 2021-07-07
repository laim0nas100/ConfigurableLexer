package lt.lb.configurablelexer.token.spec.comment;

import java.util.Objects;
import java.util.function.Supplier;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public abstract class PosAwareCommentCallback<T extends ConfToken, PosInfo> extends PositionAwareCommentCallback<T, PosInfo> {

    protected Supplier<PosInfo> posInfoSupply;

    public PosAwareCommentCallback(TokenizerCallbacks<T> delegate, Supplier<PosInfo> posInfoSupply) {
        super(delegate);
        this.posInfoSupply = Objects.requireNonNull(posInfoSupply, "posInfoSupply must not be null");
    }

    @Override
    public PosInfo getPosition() {
        return posInfoSupply.get();
    }
}
