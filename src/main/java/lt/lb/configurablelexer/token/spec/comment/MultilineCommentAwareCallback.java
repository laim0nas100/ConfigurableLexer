package lt.lb.configurablelexer.token.spec.comment;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * Caches last produced {@link ConfTokenBuffer} and checks the last token. Only
 * last token can activate comment mode;
 *
 * @author laim0nas100
 */
public abstract class MultilineCommentAwareCallback<T extends ConfToken, PosInfo> extends CommentAwareCallback<T, PosInfo> {

    protected String commentStart = "/*";
    protected String commentEnd = "*/";

    protected boolean ignoreCase = false;
    protected StringBuilder commentStartBuffer = new StringBuilder();
    protected StringBuilder commentEndingBuffer = new StringBuilder();

    public MultilineCommentAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    @Override
    public void reset() {
        commentEndingBuffer.setLength(0);
        commentStartBuffer.setLength(0);
        super.reset();
    }

    @Override
    public void charListener(boolean isTokenChar, boolean isBreakChar, int c) {
        if (withinComment) {

            if (tryMatchNewBeginningAndClear(ignoreCase, commentEndingBuffer, commentEnd, c)) {
                withinComment = false;
                lastCommentEndInfo = endComment();
                constructComment = true;
            }

        }
        if (!withinComment && !constructComment) {
            if (tryMatchNewBeginningAndClear(ignoreCase, commentStartBuffer, commentStart, c)) {
                withinComment = true;
                lastCommentStartInfo = startComment();
            }
        }

        delegate().charListener(isTokenChar, isBreakChar, c);
    }

    @Override
    public boolean isBreakChar(boolean isTokenChar, int c) {
        return !withinComment || delegate().isBreakChar(isTokenChar, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        return withinComment || delegate().isTokenChar(c);
    }

    public String getCommentStart() {
        return commentStart;
    }

    public void setCommentStart(String commentStart) {
        this.commentStart = commentStart;
    }

    public String getCommentEnd() {
        return commentEnd;
    }

    public void setCommentEnd(String commentEnd) {
        this.commentEnd = commentEnd;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }
}
