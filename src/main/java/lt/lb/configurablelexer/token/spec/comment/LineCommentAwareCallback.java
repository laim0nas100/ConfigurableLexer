package lt.lb.configurablelexer.token.spec.comment;

import java.util.function.IntPredicate;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public abstract class LineCommentAwareCallback<T extends ConfToken, PosInfo> extends CommentAwareCallback<T, PosInfo> {

    protected IntPredicate commentStart = ConfCharPredicate.ofChars('#');

    public LineCommentAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }
    
    @Override
    public void charListener(boolean isTokenChar, boolean isBreakChar, int c) {
        if (withinComment) {
            if (c == '\n') {
                withinComment = false;
                lastCommentEndInfo = endComment();
                constructComment = true;
            }
        } else if (commentStart.test(c)) {
            if (!withinComment) {
                withinComment = true;
                lastCommentStartInfo = startComment();
            }

        }
        delegate().charListener(isTokenChar, isBreakChar, c);
    }

    @Override
    public boolean isBreakChar(boolean isTokenChar, int c) {
        if (!withinComment) {
            if (commentStart.test(c)) {
                return true;
            }
        }
        return delegate().isBreakChar(isTokenChar, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        if (withinComment) {
            return c != '\n';
        }
        return delegate().isTokenChar(c);
    }

    public IntPredicate getCommentStart() {
        return commentStart;
    }

    public void setCommentStart(IntPredicate commentStart) {
        this.commentStart = commentStart;
    }

}
