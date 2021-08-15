package lt.lb.configurablelexer.token.spec.string;

import java.util.function.IntPredicate;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.spec.ExtendedPositionAwareSplittableCallbackBase;

/**
 *
 * @author laim0nas100
 */
public abstract class StringAwareCallbackBase<T extends ConfToken, PosInfo> extends ExtendedPositionAwareSplittableCallbackBase<T, PosInfo> {

    protected IntPredicate startPred = c -> c == '"';
    protected IntPredicate endPred = c -> c == '"';
    protected IntPredicate escapePred = c -> c == '\\';

    protected boolean escapeNextChar;

    public StringAwareCallbackBase(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public StringAwareCallbackBase() {
    }

    @Override
    public void resetInternalState() {
        super.resetInternalState();
        escapeNextChar = false;
    }

    @Override
    public void charListener(CharInfo chInfo, int c) {
        if (isDisabled()) {
            super.charListener(chInfo, c);
            return;
        }
        if (within) {
            if (escapeNextChar) {
                escapeNextChar = false;
            } else if (endPred.test(c)) {
                within = false;
                construct = true;
                lastEndInfo = end();
            } else if (escapePred.test(c)) {
                escapeNextChar = true;
            }
        } else if (startPred.test(c)) {
            within = true;
            lastStartInfo = start();
        }
        super.charListener(chInfo, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        return within || super.isTokenChar(c);
    }

    @Override
    public boolean isBreakChar(int c) {
        if (within) {
            return endPred.test(c); // end string and break the token
        } else { // not within
            return super.isBreakChar(c);
        }
    }

    public IntPredicate getStartPred() {
        return startPred;
    }

    public void setStartPred(IntPredicate startPred) {
        this.startPred = startPred;
    }

    public IntPredicate getEndPred() {
        return endPred;
    }

    public void setEndPred(IntPredicate endPred) {
        this.endPred = endPred;
    }

    public IntPredicate getEscapePred() {
        return escapePred;
    }

    public void setEscapePred(IntPredicate escapePred) {
        this.escapePred = escapePred;
    }
}
