package lt.lb.configurablelexer.token.spec.comment;

import java.util.function.IntPredicate;
import java.util.stream.Stream;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.DelegatingTokenizerCallbacks;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public abstract class PositionAwareCommentCallbackOld<T extends ConfToken, I> implements DelegatingTokenizerCallbacks<T> {

    protected TokenizerCallbacks<T> delegate;
    protected LineCommentAwareCallback<T, I> lcac;
    protected LineCommentAwareCallbackString<T, I> lcacs;
    protected MultilineCommentAwareCallback<T, I> mcac;
    protected TokenizerCallbacks<T> lastDecorated;
    protected boolean ignoreComments;
    protected boolean earlyReturn;
    protected boolean exclusiveComments;

    protected CommentAwareCallback exclusiveCallback;

    public PositionAwareCommentCallbackOld(TokenizerCallbacks<T> delegate) {
        this.delegate = delegate;
        this.lastDecorated = delegate;
    }

    public boolean isIgnoreComments() {
        return ignoreComments;
    }

    public void setIgnoreComments(final boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
        Stream.of(lcac, lcacs, mcac).filter(f -> f != null).forEach(f -> f.setIgnore(ignoreComments));
    }

    public boolean isExclusiveComments() {
        return exclusiveComments;
    }

    public void setExclusiveComments(boolean exclusiveComments) {
        this.exclusiveComments = exclusiveComments;
    }

    public boolean isEarlyReturn() {
        return earlyReturn;
    }

    public void setEarlyReturn(boolean earlyReturn) {
        this.earlyReturn = earlyReturn;
        Stream.of(lcac, lcacs, mcac).filter(f -> f != null).forEach(f -> f.setEarlyReturn(earlyReturn));
    }

    public abstract I getPosition();

    public abstract T contructComment(I start, I end, char[] buffer, int offset, int length) throws Exception;

    public PositionAwareCommentCallbackOld<T, I> ignoringComments(boolean ignoreComments) {
        setIgnoreComments(ignoreComments);
        return this;
    }

    public PositionAwareCommentCallbackOld<T, I> enableCommentExclusion(boolean exclusion) {
        setExclusiveComments(exclusion);
        return this;
    }

    public PositionAwareCommentCallbackOld<T, I> enableMultilineComment(String commentStartEnd) {
        return enableMultilineComment(commentStartEnd, commentStartEnd, false);
    }

    public PositionAwareCommentCallbackOld<T, I> enableMultilineComment(String commentStart, String commentEnd) {
        return enableMultilineComment(commentStart, commentEnd, false);
    }

    protected void resetAllBut(CommentAwareCallback ob) {
        Stream.of(lcac, lcacs, mcac).filter(f -> f != null && f != ob).forEach(f -> f.resetInternalState());
    }

    public PositionAwareCommentCallbackOld<T, I> enableMultilineComment(String commentStart, String commentEnd, boolean ignoreCase) {
        PositionAwareCommentCallbackOld<T, I> me = this;
        if (mcac == null) {
            mcac = new MultilineCommentAwareCallback<T, I>(lastDecorated) {
                @Override
                public boolean isDisabled() {
                    if (exclusiveComments) {
                        return (exclusiveCallback != null && exclusiveCallback != this);
                    }
                    return super.isDisabled();
                }

                @Override
                public I start() {
                    if (exclusiveComments) {
                        resetAllBut(this);
                        assert exclusiveCallback == null;
                        exclusiveCallback = this;
                    }
                    return getPosition();
                }

                @Override
                public I mid() {
                    return getPosition();
                }

                @Override
                public I end() {
                    if (exclusiveComments) {
                        exclusiveCallback = null;
                    }
                    return getPosition();
                }

                @Override
                public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
                    return me.contructComment(start, end, buffer, offset, length);
                }
            };
            lastDecorated = mcac;
        }
        mcac.setIgnoreCase(ignoreCase);
        mcac.setCommentStart(commentStart);
        mcac.setCommentEnd(commentEnd);
        mcac.setIgnore(ignoreComments);
        mcac.setEarlyReturn(earlyReturn);

        return this;
    }

    public PositionAwareCommentCallbackOld<T, I> enableLineComment(String commentStart) {
        return enableLineComment(commentStart, false);
    }

    public PositionAwareCommentCallbackOld<T, I> enableLineComment(String commentStart, boolean ignoreCase) {
        PositionAwareCommentCallbackOld<T, I> me = this;
        if (lcacs == null) {
            lcacs = new LineCommentAwareCallbackString<T, I>(lastDecorated) {
                @Override
                public boolean isDisabled() {
                    if (exclusiveComments) {
                        return (exclusiveCallback != null && exclusiveCallback != this);
                    }
                    return super.isDisabled();
                }

                @Override
                public I start() {
                    if (exclusiveComments) {
                        resetAllBut(this);
                        assert exclusiveCallback == null;
                        exclusiveCallback = this;

                    }
                    return getPosition();
                }

                @Override
                public I mid() {
                    return getPosition();
                }

                @Override
                public I end() {
                    if (exclusiveComments) {
                        exclusiveCallback = null;
                    }
                    return getPosition();
                }

                @Override
                public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
                    return me.contructComment(start, end, buffer, offset, length);
                }
            };
            lastDecorated = lcacs;
        }
        lcacs.setCommentStart(commentStart);
        lcacs.setIgnoreCase(ignoreCase);
        lcacs.setIgnore(ignoreComments);
        lcacs.setEarlyReturn(earlyReturn);

        return this;
    }

    public PositionAwareCommentCallbackOld<T, I> enableLineComment(int... possibleCommentChars) {
        return enableLineComment(ConfCharPredicate.ofChars(possibleCommentChars));
    }

    public PositionAwareCommentCallbackOld<T, I> enableLineComment(IntPredicate charPredicate) {
        PositionAwareCommentCallbackOld<T, I> me = this;
        if (lcac == null) {
            lcac = new LineCommentAwareCallback<T, I>(lastDecorated) {
                @Override
                public boolean isDisabled() {
                    if (exclusiveComments) {
                        return (exclusiveCallback != null && exclusiveCallback != this);
                    }
                    return super.isDisabled();
                }

                @Override
                public I start() {
                    if (exclusiveComments) {
                        resetAllBut(this);
                        assert exclusiveCallback == null;
                        exclusiveCallback = this;
                    }
                    return getPosition();
                }

                @Override
                public I mid() {
                    return getPosition();
                }

                @Override
                public I end() {
                    if (exclusiveComments) {
                        exclusiveCallback = null;
                    }
                    return getPosition();
                }

                @Override
                public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
                    return me.contructComment(start, end, buffer, offset, length);
                }
            };
            lastDecorated = lcac;
        }
        lcac.setIgnore(ignoreComments);
        lcac.setCommentStart(charPredicate);
        lcac.setEarlyReturn(earlyReturn);
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
