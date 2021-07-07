package lt.lb.configurablelexer.token.spec.comment;

import java.util.function.IntPredicate;
import lt.lb.configurablelexer.Redirecter;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.spec.ExtendedPositionAwareExclusiveCallback;

/**
 *
 * @author laim0nas100
 * @param <T>
 * @param <I>
 */
public abstract class PositionAwareCommentCallback<T extends ConfToken, I> extends ExtendedPositionAwareExclusiveCallback<T, I> {

    protected LineCommentAwareCallback<T, I> lcac;
    protected LineCommentAwareCallbackString<T, I> lcacs;
    protected MultilineCommentAwareCallback<T, I> mcac;

    public PositionAwareCommentCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public boolean isIgnoreComments() {
        return isIgnore();
    }

    public void setIgnoreComments(final boolean ignoreComments) {
        setIgnore(ignoreComments);
    }

    public boolean isExclusiveComments() {
        return isExclusive();
    }

    public void setExclusiveComments(boolean exclusiveComments) {
        setExclusive(exclusiveComments);
    }

    public abstract T contructComment(I start, I end, char[] buffer, int offset, int length) throws Exception;

    @Override
    public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
        return contructComment(start, end, buffer, offset, length);
    }

    public PositionAwareCommentCallback<T, I> ignoringComments(boolean ignoreComments) {
        setIgnoreComments(ignoreComments);
        return this;
    }

    public PositionAwareCommentCallback<T, I> enableCommentExclusion(boolean exclusion) {
        setExclusiveComments(exclusion);
        return this;
    }

    public PositionAwareCommentCallback<T, I> enableMultilineComment(String commentStartEnd) {
        return enableMultilineComment(commentStartEnd, commentStartEnd, false);
    }

    public PositionAwareCommentCallback<T, I> enableMultilineComment(String commentStart, String commentEnd) {
        return enableMultilineComment(commentStart, commentEnd, false);
    }

    public PositionAwareCommentCallback<T, I> enableMultilineComment(String commentStart, String commentEnd, boolean ignoreCase) {
        PositionAwareCommentCallback<T, I> me = this;
        if (mcac == null) {
            mcac = this.addNested((e, f) -> {
                return new MultilineCommentAwareCallback<T, I>(f) {

                    Redirecter.RedirecterSupplier<Boolean> redir;

                    @Override
                    public boolean isDisabled() {
                        if (redir == null) {
                            redir = Redirecter.of(() -> e.isDisabled(), () -> super.isDisabled());
                        }
                        return redir.get();
                    }

                    @Override
                    public I start() {
                        return e.start();
                    }

                    @Override
                    public I mid() {
                        return e.mid();
                    }

                    @Override
                    public I end() {
                        return e.end();
                    }

                    @Override
                    public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
                        return e.construct(start, end, buffer, offset, length);
                    }
                };
            });
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
            lcacs = this.addNested((e, f) -> {
                return new LineCommentAwareCallbackString<T, I>(f) {
                    Redirecter.RedirecterSupplier<Boolean> redir;

                    @Override
                    public boolean isDisabled() {
                        if (redir == null) {
                            redir = Redirecter.of(() -> e.isDisabled(), () -> super.isDisabled());
                        }
                        return redir.get();
                    }

                    @Override
                    public I start() {
                        return e.start();
                    }

                    @Override
                    public I mid() {
                        return e.mid();
                    }

                    @Override
                    public I end() {
                        return e.end();
                    }

                    @Override
                    public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
                        return e.construct(start, end, buffer, offset, length);
                    }
                };
            });
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
            lcac = this.addNested((e, f) -> {
                return new LineCommentAwareCallback<T, I>(f) {

                    Redirecter.RedirecterDisableAware redir;

                    @Override
                    public boolean isDisabled() {
                        if (redir == null) {
                            redir = new Redirecter.RedirecterDisableAware(e, () -> super.isDisabled());
                        }
                        return redir.isDisabled();
                    }

                    @Override
                    public I start() {
                        return e.start();
                    }

                    @Override
                    public I mid() {
                        return e.mid();
                    }

                    @Override
                    public I end() {
                        return e.end();
                    }

                    @Override
                    public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
                        return e.construct(start, end, buffer, offset, length);
                    }
                };
            });
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
