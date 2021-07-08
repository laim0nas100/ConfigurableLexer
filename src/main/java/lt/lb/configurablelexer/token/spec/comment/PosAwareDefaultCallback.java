package lt.lb.configurablelexer.token.spec.comment;

import java.util.Objects;
import java.util.function.Supplier;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.spec.PositionAwareDefaultCallback;

/**
 *
 * @author laim0nas100
 */
public abstract class PosAwareDefaultCallback<T extends ConfToken, PosInfo> extends PositionAwareDefaultCallback<T, PosInfo> {

    protected Supplier<PosInfo> posInfoSupply;

    public PosAwareDefaultCallback(TokenizerCallbacks<T> delegate, Supplier<PosInfo> posInfoSupply) {
        super(delegate);
        this.posInfoSupply = Objects.requireNonNull(posInfoSupply, "posInfoSupply must not be null");
    }

    @Override
    public PosInfo getPosition() {
        return posInfoSupply.get();
    }
}
