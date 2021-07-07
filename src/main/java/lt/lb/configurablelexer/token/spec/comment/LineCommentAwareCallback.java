package lt.lb.configurablelexer.token.spec.comment;

import java.util.function.IntPredicate;
import lt.lb.commons.DLog;
import lt.lb.configurablelexer.token.CharInfo;
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
    public void charListener(CharInfo chInfo, int c) {
        if (isDisabled()) {
            super.charListener(chInfo, c);
            return;
        }
        if (within) {
            if (c == '\n') {
                within = false;
//                DLog.print("END LineCommentAwareCallback");
                lastEndInfo = end();
                construct = true;
            }
        } else if (commentStart.test(c)) {
            within = true;
//            DLog.print("START LineCommentAwareCallback");
            lastStartInfo = start();

        }
        super.charListener(chInfo, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        if (within) {
            return c != '\n';
        }
        return super.isTokenChar(c);
    }

    public IntPredicate getCommentStart() {
        return commentStart;
    }

    public void setCommentStart(IntPredicate commentStart) {
        this.commentStart = commentStart;
    }

}
