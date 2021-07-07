package lt.lb.configurablelexer.token.spec.string;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntPredicate;
import lt.lb.configurablelexer.token.BaseDelegatingTokenizerCallbacks;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public abstract class StringAwareCallback<T extends ConfToken, PosInfo> extends BaseDelegatingTokenizerCallbacks<T> {

    public StringAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public StringAwareCallback() {
    }

    protected IntPredicate stringStart = c -> c == '"';
    protected IntPredicate stringEnd = c -> c == '"';
    protected IntPredicate stringEscape = c -> c == '\\';

    protected PosInfo lastStringStartInfo;
    protected PosInfo lastStringEndInfo;
    protected boolean earlyReturn;
    protected List<T> unsubmittedStrings = new ArrayList<>();
    protected boolean withinString;
    protected boolean escapeNextChar;
    protected boolean constructString;

    @Override
    public void charListener(CharInfo chInfo, int c) {
         if (isDisabled()) {
            super.charListener(chInfo, c);
            return;
        }
        if (withinString) {
            if (escapeNextChar) {
                escapeNextChar = false;
            } else if (stringEnd.test(c)) {
                withinString = false;
                constructString = true;
                lastStringEndInfo = endString();
            } else if (stringEscape.test(c)) {
                escapeNextChar = true;
            }
        } else if (stringStart.test(c)) {
            withinString = true;
            lastStringStartInfo = startString();
        }
        super.charListener(chInfo, c);
    }

    public abstract PosInfo startString();

    public abstract PosInfo endString();

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        if (withinString) {// early token split
            // not yet out of string, split the string, return only when fully read the string
            unsubmittedStrings.add(constructString(lastStringStartInfo, lastStringEndInfo, buffer, offset, length));
            if (earlyReturn) {
                ConfTokenBuffer<T> ofList = ConfTokenBuffer.ofList(new ArrayList<>(unsubmittedStrings));
                unsubmittedStrings.clear();
                return ofList;
            }
            return ConfTokenBuffer.empty();
        } else if (constructString) {
            unsubmittedStrings.add(constructString(lastStringStartInfo, lastStringEndInfo, buffer, offset, length));
            constructString = false;
            ConfTokenBuffer<T> ofList = ConfTokenBuffer.ofList(new ArrayList<>(unsubmittedStrings));
            unsubmittedStrings.clear();
            return ofList;

        } else {
            return super.constructTokens(buffer, offset, length);
        }
    }

    @Override
    public boolean isTokenChar(int c) {
        return withinString || super.isTokenChar(c);
    }

    @Override
    public boolean isBreakChar(int c) {
        return !withinString && super.isBreakChar( c);
    }

    public abstract T constructString(PosInfo start, PosInfo end, char[] buffer, int offset, int length) throws Exception;

}
