package lt.lb.configurablelexer.token.spec;

import java.util.ArrayList;
import java.util.List;
import lt.lb.configurablelexer.token.BaseDelegatingTokenizerCallbacks;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 * @param <T>
 * @param <PosInfo>
 */
public abstract class ExtendedPositionAwareSplittableCallbackBase<T extends ConfToken, PosInfo> extends BaseDelegatingTokenizerCallbacks<T> implements ExtendedPositionAwareSplittableCallback<T, PosInfo> {

    public ExtendedPositionAwareSplittableCallbackBase(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public ExtendedPositionAwareSplittableCallbackBase() {
    }

    @Override
    public void reset() {
        resetInternalState();
        delegate().reset();
    }

    @Override
    public void resetInternalState() {
        construct = false;
        unsubmitted.clear();
        within = false;
        lastEndInfo = null;
        lastStartInfo = null;
        lastSplitInfo = null;
    }

    protected PosInfo lastStartInfo;
    protected PosInfo lastEndInfo;
    protected PosInfo lastSplitInfo;
    protected boolean construct;
    protected boolean within;
    protected boolean ignore;
    protected boolean earlyReturn;
    protected List<T> unsubmitted = new ArrayList<>();

    @Override
    public ConfTokenBuffer constructTokens(char[] buffer, int offset, int length) throws Exception {
        if (isDisabled()) {
            return super.constructTokens(buffer, offset, length);
        }
        if (within) {// early token split
            // not yet out of comment, split the comment
            if (!ignore) {
                PosInfo newMid = mid();
                PosInfo startingPos = unsubmitted.isEmpty() ? lastStartInfo : lastSplitInfo;
                unsubmitted.add(construct(startingPos, newMid, buffer, offset, length));
                lastSplitInfo = newMid;
            }
            if (earlyReturn) {
                ConfTokenBuffer<T> ofList = ConfTokenBuffer.ofList(new ArrayList<>(unsubmitted));
                unsubmitted.clear();
                return ofList;
            }

            return ConfTokenBuffer.empty();
        } else if (construct) {
            if (!ignore) {
                PosInfo startingPos = unsubmitted.isEmpty() ? lastStartInfo : lastSplitInfo;
                unsubmitted.add(construct(startingPos, lastEndInfo, buffer, offset, length));

            }
            construct = false;
            if (ignore) {
                return ConfTokenBuffer.empty();
            }
            ConfTokenBuffer<T> ofList = ConfTokenBuffer.ofList(new ArrayList<>(unsubmitted));
            unsubmitted.clear();
            return ofList;

        } else {
            return super.constructTokens(buffer, offset, length);
        }
    }

    @Override
    public boolean isIgnore() {
        return ignore;
    }

    @Override
    public void setIgnore(boolean ignore) {
        this.ignore = ignore;
    }

    @Override
    public boolean isWithin() {
        return within;
    }

    @Override
    public boolean isEarlyReturn() {
        return earlyReturn;
    }

    @Override
    public void setEarlyReturn(boolean earlyReturn) {
        this.earlyReturn = earlyReturn;
    }

    @Override
    public abstract T construct(PosInfo start, PosInfo end, char[] buffer, int offset, int length) throws Exception;

    /**
     * Adds new codepoint to the buffer, if new buffer is of the same length as
     * is toMatch
     *
     * @param ignoreCase
     * @param buffer to add new codepoint and match chars
     * @param toMatch full string to match
     * @param codePoint what codepoint to add to the given buffer before
     * matching
     *
     * @return
     */
    public static boolean tryMatchNewBeginningAndClear(boolean ignoreCase, StringBuilder buffer, String toMatch, int codePoint) {
        boolean matched = false;
        buffer.appendCodePoint(codePoint);
        int len = buffer.length();
        int tLen = toMatch.length();
        if (len > tLen) {
            buffer.setLength(0);
            return false;
        }
        boolean regionMatches = toMatch.regionMatches(ignoreCase, 0, buffer.toString(), 0, len);

        if (regionMatches) {
            if (len == tLen) {
                matched = true;
                buffer.setLength(0);
            }  // not all correct, but in progress, don't reset

        } else {
            buffer.setLength(0);
        }
        return matched;
    }

    /**
     * Adds new codepoint to the new buffer, if new buffer is of the same length
     * as is toMatch. Does not change the passed buffer, unlike the other method      {@link tryMatchNewBeginningAndClear(boolean, StringBuilder, String, int)
     * }
     *
     * @param ignoreCase
     * @param currentBuffer
     * @param toMatch full string to match
     * @param codePoint what codepoint to add to the new buffer before matching
     *
     * @return
     */
    public static boolean tryMatchBeginning(boolean ignoreCase, StringBuilder currentBuffer, String toMatch, int codePoint) {
        boolean matched = false;
        StringBuilder buffer = new StringBuilder(currentBuffer).appendCodePoint(codePoint);
        int len = buffer.length();
        int tLen = toMatch.length();
        if (len > tLen) {
            return false;
        }
        boolean regionMatches = toMatch.regionMatches(ignoreCase, 0, buffer.toString(), 0, len);

        if (regionMatches) {
            if (len == tLen) {
                matched = true;
            }
        }
        return matched;
    }

}
