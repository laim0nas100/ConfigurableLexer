package lt.lb.configurablelexer.token.spec.comment;

import java.util.function.IntPredicate;
import java.util.function.Supplier;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.DelegatingTokenizerCallbacks;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public abstract class PositionAwareCommentCallback<T extends ConfToken, I> implements DelegatingTokenizerCallbacks<T> {

    protected TokenizerCallbacks<T> delegate;
    protected LineCommentAwareCallback<T, I> lcac;
    protected LineCommentAwareCallbackString<T, I> lcacs;
    protected MultilineCommentAwareCallback<T, I> mcac;
    protected TokenizerCallbacks<T> lastDecorated;

    public PositionAwareCommentCallback(TokenizerCallbacks<T> delegate) {
        this.delegate = delegate;
        this.lastDecorated = delegate;
    }

    public abstract I getPosition();

    public abstract T contructComment(I start, I end, char[] buffer, int offset, int length) throws Exception;

    public PositionAwareCommentCallback<T, I> enableMultilineComment(String commentStartEnd){
        return enableMultilineComment(commentStartEnd, commentStartEnd, false);
    }
    
    public PositionAwareCommentCallback<T, I> enableMultilineComment(String commentStart, String commentEnd){
        return enableMultilineComment(commentStart, commentEnd, false);
    }
    
    public PositionAwareCommentCallback<T, I> enableMultilineComment(String commentStart, String commentEnd, boolean ignoreCase) {
        PositionAwareCommentCallback<T, I> me = this;
        if (mcac == null) {
            mcac = new MultilineCommentAwareCallback<T, I>(lastDecorated) {
                @Override
                public I startComment() {
                    return getPosition();
                }

                @Override
                public I endComment() {
                    return getPosition();
                }

                @Override
                public T contructComment(I start, I end, char[] buffer, int offset, int length) throws Exception {
                    return me.contructComment(start, end, buffer, offset, length);
                }
            };
            lastDecorated = mcac;
        }
        mcac.setIgnoreCase(ignoreCase);
        mcac.setCommentStart(commentStart);
        mcac.setCommentEnd(commentEnd);

        return this;
    }

    public PositionAwareCommentCallback<T, I> enableLineComment(String commentStart) {
        return enableLineComment(commentStart, false);
    }

    public PositionAwareCommentCallback<T, I> enableLineComment(String commentStart, boolean ignoreCase) {
        PositionAwareCommentCallback<T, I> me = this;
        if (lcacs == null) {
            lcacs = new LineCommentAwareCallbackString<T, I>(lastDecorated) {
                @Override
                public I startComment() {
                    return getPosition();
                }

                @Override
                public I endComment() {
                    return getPosition();
                }

                @Override
                public T contructComment(I start, I end, char[] buffer, int offset, int length) throws Exception {
                    return me.contructComment(start, end, buffer, offset, length);
                }
            };
            lastDecorated = lcacs;
        }
        lcacs.setCommentStart(commentStart);
        lcacs.setIgnoreCase(ignoreCase);

        return this;
    }

    public PositionAwareCommentCallback<T, I> enableLineComment(int... possibleCommentChars) {
        return enableLineComment(ConfCharPredicate.ofChars(possibleCommentChars));
    }

    public PositionAwareCommentCallback<T, I> enableLineComment(IntPredicate charPredicate) {
        PositionAwareCommentCallback<T, I> me = this;
        if (lcac == null) {
            lcac = new LineCommentAwareCallback<T, I>(lastDecorated) {
                @Override
                public I startComment() {
                    return getPosition();
                }

                @Override
                public I endComment() {
                    return getPosition();
                }

                @Override
                public T contructComment(I start, I end, char[] buffer, int offset, int length) throws Exception {
                    return me.contructComment(start, end, buffer, offset, length);
                }
            };
            lastDecorated = lcac;
        }

        lcac.setCommentStart(charPredicate);
        return this;
    }

    public LineCommentAwareCallback<T, I> getLineCommentAwareCallback() {
        return lcac;
    }

    public LineCommentAwareCallbackString<T, I> getLineCommentAwareCallbackString() {
        return lcacs;
    }

    public MultilineCommentAwareCallback<T, I> getMultilineCommentAwareCallback() {
        return mcac;
    }

    @Override
    public TokenizerCallbacks<T> delegate() {
        return lastDecorated;
    }

}
