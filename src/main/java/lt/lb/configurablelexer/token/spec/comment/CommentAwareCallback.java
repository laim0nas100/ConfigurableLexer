package lt.lb.configurablelexer.token.spec.comment;

import lt.lb.configurablelexer.token.DelegatingTokenizerCallbacks;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;

/**
 *
 * @author laim0nas100
 */
public abstract class CommentAwareCallback<T extends ConfToken, PosInfo> implements DelegatingTokenizerCallbacks<T> {

    public CommentAwareCallback(TokenizerCallbacks<T> delegate) {
        this.delegate = delegate;
    }

    public CommentAwareCallback() {
    }

    public void setDelegate(TokenizerCallbacks<T> delegate) {
        this.delegate = delegate;
    }
    

    @Override
    public void reset() {
        constructComment = false;
        delegate().reset();
    }

    protected PosInfo lastCommentStartInfo;
    protected PosInfo lastCommentEndInfo;
    protected boolean constructComment;
    protected boolean withinComment;
    protected TokenizerCallbacks<T> delegate;

    @Override
    public TokenizerCallbacks<T> delegate() {
        return delegate;
    }

    public abstract PosInfo startComment();

    public abstract PosInfo endComment();

    @Override
    public ConfTokenBuffer constructTokens(char[] buffer, int offset, int length) throws Exception {
        if (constructComment) {
            ConfTokenBuffer<T> buff = ConfTokenBuffer.of(contructComment(lastCommentStartInfo, lastCommentEndInfo, buffer, offset, length));
            constructComment = false;
            return buff;

        }
        return delegate().constructTokens(buffer, offset, length);
    }

    @Override
    public boolean isBreakChar(boolean isTokenChar, int c) {
        return !withinComment || delegate().isBreakChar(isTokenChar, c);
    }

    public abstract T contructComment(PosInfo start, PosInfo end, char[] buffer, int offset, int length) throws Exception;

    /**
     * Adds new codepoint to the buffer, if new buffer is of the same length as
     * is toMatch
     *
     * @param ignoreCase
     * @param buffer to add new codepoint and match chars
     * @param toMatch full string to match
     * @param codePoint what codepoint to add ti the given buffer before
     * matching
     *
     * @return
     */
    public static boolean tryMatchNewBeginningAndClear(boolean ignoreCase, StringBuilder buffer, String toMatch, int codePoint) {
        boolean withinComment = false;
        buffer.appendCodePoint(codePoint);
        int len = buffer.length();
        if (len > toMatch.length()) {
            buffer.setLength(0);
            return false;
        }
        boolean regionMatches = toMatch.regionMatches(ignoreCase, 0, buffer.toString(), 0, len);

        if (regionMatches) {
            if (len == toMatch.length()) {
                withinComment = true;
                buffer.setLength(0);
            }  // not all correct, but in progress

        } else {
            buffer.setLength(0);
        }
        return withinComment;
    }
}
