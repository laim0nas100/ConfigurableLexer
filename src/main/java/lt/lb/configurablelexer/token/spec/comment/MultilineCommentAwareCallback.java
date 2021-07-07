package lt.lb.configurablelexer.token.spec.comment;

import lt.lb.commons.DLog;
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

    public MultilineCommentAwareCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }


    @Override
    public void resetInternalState() {
        super.resetInternalState();
        commentBufferEnd.setLength(0);
        commentBufferStart.setLength(0);
    }
    
    @Override
    public void charListener(CharInfo chInfo, int c) {
        if (isDisabled()) {
            super.charListener(chInfo, c);
            return;
        }
        if (within) {
            if (tryMatchNewBeginningAndClear(ignoreCase, commentBufferEnd, commentEnd, c)) {
                within = false;
//                 DLog.print("END MultilineCommentAwareCallback");
                lastEndInfo = end();
                construct = true;
            }
        }
        if (!within && !construct) {
            if (tryMatchNewBeginningAndClear(ignoreCase, commentBufferStart, commentStart, c)) {
                within = true;
//                 DLog.print("START MultilineCommentAwareCallback");
                lastStartInfo = start();
            }
        }

        super.charListener(chInfo, c);
    }

    @Override
    public boolean isTokenChar(int c) {
        if(isDisabled()){
            return super.isTokenChar(c);
        }
        return within || super.isTokenChar(c);
    }

    @Override
    public boolean isBreakChar(int c) {
        if(isDisabled()){
            return super.isBreakChar(c);
        }
        return super.isBreakChar(c) || tryMatchBeginning(ignoreCase, commentBufferEnd, commentEnd, c);
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
