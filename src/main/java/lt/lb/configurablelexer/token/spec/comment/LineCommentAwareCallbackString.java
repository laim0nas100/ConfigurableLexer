package lt.lb.configurablelexer.token.spec.comment;

import lt.lb.commons.DLog;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * Caches and checks the string for comment activation.
 *
 * @author laim0nas100
 */
public abstract class LineCommentAwareCallbackString<T extends ConfToken, I> extends CommentAwareCallback<T, I> {

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
    public void resetInternalState() {
        super.resetInternalState();
        commentPrefixBuffer.setLength(0);
    }

    @Override
    public void charListener(CharInfo chInfo, int c) {
        if (isDisabled()) {
            super.charListener(chInfo, c);
            return;
        }
        if (within && c == '\n') {
            within = false;
//            DLog.print("END LineCommentAwareCallbackString");
            lastEndInfo = end();
            construct = true;
        }
        if (!construct) {// not ended comment
            if (tryMatchNewBeginningAndClear(ignoreCase, commentPrefixBuffer, commentStart, c)) {
                within = true;
//                 DLog.print("START LineCommentAwareCallbackString");
                lastStartInfo = start();
            }
        }

        super.charListener(chInfo, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        if (isDisabled()) {
            return super.isTokenChar(c);
        }
        if (within) {
            return c != '\n';
        }
        return super.isTokenChar(c);
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
