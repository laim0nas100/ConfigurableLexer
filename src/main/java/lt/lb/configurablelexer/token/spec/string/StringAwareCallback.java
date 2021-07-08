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
public abstract class StringAwareCallback<T extends ConfToken, PosInfo> extends ExtendedPositionAwareSplittableCallbackBase<T,PosInfo> {

    public StringAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public StringAwareCallback() {
    }

    protected IntPredicate stringStart = c -> c == '"';
    protected IntPredicate stringEnd = c -> c == '"';
    protected IntPredicate stringEscape = c -> c == '\\';

    protected boolean escapeNextChar;

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
            } else if (stringEnd.test(c)) {
                within = false;
                construct = true;
                lastEndInfo = end();
            } else if (stringEscape.test(c)) {
                escapeNextChar = true;
            }
        } else if (stringStart.test(c)) {
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
        return !within && super.isBreakChar( c);
    }

    public IntPredicate getStringStart() {
        return stringStart;
    }

    public void setStringStart(IntPredicate stringStart) {
        this.stringStart = stringStart;
    }

    public IntPredicate getStringEnd() {
        return stringEnd;
    }

    public void setStringEnd(IntPredicate stringEnd) {
        this.stringEnd = stringEnd;
    }

    public IntPredicate getStringEscape() {
        return stringEscape;
    }

    public void setStringEscape(IntPredicate stringEscape) {
        this.stringEscape = stringEscape;
    }
    
    

}
