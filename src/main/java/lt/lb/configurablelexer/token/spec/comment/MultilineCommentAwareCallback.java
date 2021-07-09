package lt.lb.configurablelexer.token.spec.comment;

import lt.lb.configurablelexer.token.CharInfo;
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
    protected StringBuilder commentBufferStart = new StringBuilder();
    protected StringBuilder commentBufferEnd = new StringBuilder();
    protected boolean canBeNested = false;
    protected int nest = 0;

    public MultilineCommentAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    @Override
    public void resetInternalState() {
        super.resetInternalState();
        commentBufferEnd.setLength(0);
        commentBufferStart.setLength(0);
        nest = 0;
    }

    @Override
    public void charListener(CharInfo chInfo, int c) {
        if (isDisabled()) {
            super.charListener(chInfo, c);
            return;
        }
        if (within) {
            if (tryMatchNewBeginningAndClear(ignoreCase, commentBufferEnd, commentEnd, c)) {
                if (canBeNested) {
                    nest--;
                }

                if (nest == 0) {
                    within = false;
                    lastEndInfo = end();
                    construct = true;
                }

            }
        }
        if ((!within || canBeNested) && !construct) {
            if (tryMatchNewBeginningAndClear(ignoreCase, commentBufferStart, commentStart, c)) {
                if (nest == 0) {
                    within = true;
                    lastStartInfo = start();
                }
                if (canBeNested) {
                    nest++;
                }

            }
        }

        super.charListener(chInfo, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        if (isDisabled()) {
            return super.isTokenChar(c);
        }
        return within || super.isTokenChar(c);
    }

    @Override
    public boolean isBreakChar(int c) {
        if (isDisabled()) {
            return super.isBreakChar(c);
        }
        
        return super.isBreakChar(c) || (nest == 0 && tryMatchBeginning(ignoreCase, commentBufferEnd, commentEnd, c));
    }

    public String getCommentStart() {
        return commentStart;
    }

    public void setCommentStart(String commentStart) {
        this.commentStart = commentStart;
    }

    public boolean isCanBeNested() {
        return canBeNested;
    }

    public void setCanBeNested(boolean canBeNested) {
        this.canBeNested = canBeNested;
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
