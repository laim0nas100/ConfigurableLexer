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
public abstract class LineCommentAwareCallbackString<T extends ConfToken,I> extends CommentAwareCallback<T,I> {

    protected String commentStart = "//";
    protected boolean ignoreCase = false;
    protected StringBuilder commentPrefixBuffer = new StringBuilder();

    public LineCommentAwareCallbackString(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public LineCommentAwareCallbackString(String commentStart, TokenizerCallbacks<T> delegate) {
        super(delegate);
        this.commentStart = commentStart;
    }

    @Override
    public void reset() {
        commentPrefixBuffer.setLength(0);
    }

    @Override
    public void charListener(boolean isTokenChar, boolean isBreakChar, int c) {
        if (withinComment && c == '\n') {
            withinComment = false;
            lastCommentEndInfo = endComment();
            constructComment = true;
        }
        if (!constructComment) {// not ended comment
            if (tryMatchNewBeginningAndClear(ignoreCase, commentPrefixBuffer, commentStart, c)) {
                withinComment = true;
                lastCommentStartInfo = startComment();
            }
        }

        delegate().charListener(isTokenChar, isBreakChar, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        if (withinComment) {
            return c != '\n';
        }
        return delegate().isTokenChar(c);
    }
    
    public String getCommentStart() {
        return commentStart;
    }

    public void setCommentStart(String commentStart) {
        this.commentStart = commentStart;
    }

    public boolean isIgnoreCase() {
        return ignoreCase;
    }

    public void setIgnoreCase(boolean ignoreCase) {
        this.ignoreCase = ignoreCase;
    }

}
