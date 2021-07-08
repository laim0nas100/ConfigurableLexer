package lt.lb.configurablelexer.token.spec;

import java.util.function.IntPredicate;
import java.util.stream.Stream;
import lt.lb.configurablelexer.Redirecter;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.spec.comment.CommentAwareCallback;
import lt.lb.configurablelexer.token.spec.comment.LineCommentAwareCallback;
import lt.lb.configurablelexer.token.spec.comment.LineCommentAwareCallbackString;
import lt.lb.configurablelexer.token.spec.comment.MultilineCommentAwareCallback;
import lt.lb.configurablelexer.token.spec.string.StringAwareCallback;

/**
 *
 * @author laim0nas100
 * @param <T>
 * @param <I>
 */
public abstract class PositionAwareDefaultCallback<T extends ConfToken, I> extends ExtendedPositionAwareExclusiveCallbackAggregate<T, I> {

    protected LineCommentAwareCallback<T, I> lcac;
    protected LineCommentAwareCallbackString<T, I> lcacs;
    protected MultilineCommentAwareCallback<T, I> mcac;
    protected StringAwareCallback<T, I> strings;
    protected boolean ignoreComments;

    public PositionAwareDefaultCallback(TokenizerCallbacks<T> delegate) {
        super(delegate);
    }

    public boolean isIgnoreComments() {
        return ignoreComments;
    }

    protected Stream<CommentAwareCallback<T, I>> getCommentCallbacks() {
        return getCallbackStream().filter(f -> f instanceof CommentAwareCallback).map(m -> (CommentAwareCallback<T, I>) m);
    }

    public void setIgnoreComments(final boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
        getCommentCallbacks().forEach(f -> {
            ExtendedPositionAwareSplittableCallback cb = f;
            cb.setIgnore(ignoreComments);
        });
    }

    public boolean isExclusiveComments() {
        return isExclusive();
    }


    public abstract T contructComment(I start, I end, char[] buffer, int offset, int length) throws Exception;
    
    public abstract T contructString(I start, I end, char[] buffer, int offset, int length) throws Exception;

    public PositionAwareDefaultCallback<T, I> ignoringOnlyComments(boolean ignoreComments) {
        setIgnoreComments(ignoreComments);
        return this;
    }

    @Override
    public PositionAwareDefaultCallback<T, I> enableExclusion(boolean exclusion) {
        setExclusive(exclusion);
        return this;
    }

    public PositionAwareDefaultCallback<T, I> enableMultilineComment(String commentStartEnd) {
        return enableMultilineComment(commentStartEnd, commentStartEnd, false);
    }

    public PositionAwareDefaultCallback<T, I> enableMultilineComment(String commentStart, String commentEnd) {
        return enableMultilineComment(commentStart, commentEnd, false);
    }

    public PositionAwareDefaultCallback<T, I> enableMultilineComment(String commentStart, String commentEnd, boolean ignoreCase) {
        if (mcac == null) {
            PositionAwareDefaultCallback<T, I> me = this;
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
                        return contructComment(start, end, buffer, offset, length);
                    }
                };
            });
        }
        mcac.setIgnoreCase(ignoreCase);
        mcac.setCommentStart(commentStart);
        mcac.setCommentEnd(commentEnd);
        mcac.setIgnore(ignore || ignoreComments);

        return this;
    }
    
    public PositionAwareDefaultCallback<T, I> enableStrings(){
        return enableStrings('"', '\\');
    }
    
    public PositionAwareDefaultCallback<T, I> enableStrings(int endings, int escape){
        IntPredicate ofChars = ConfCharPredicate.ofChars(endings);
        return enableStrings(ofChars, ofChars, ConfCharPredicate.ofChars(escape));
    }
    
    public PositionAwareDefaultCallback<T, I> enableStrings(int start, int end, int escape){
        return enableStrings(ConfCharPredicate.ofChars(start), ConfCharPredicate.ofChars(end), ConfCharPredicate.ofChars(escape));
    }
    
     public PositionAwareDefaultCallback<T, I> enableStrings(IntPredicate start, IntPredicate end, IntPredicate escape) {
        if (strings == null) {
            strings = this.addNested((e, f) -> {
                return new StringAwareCallback<T, I>(f) {

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
                        return contructString(start, end, buffer, offset, length);
                    }
                };
            });
        }
        strings.setStringEnd(end);
        strings.setStringStart(start);
        strings.setStringEscape(escape);
        strings.setIgnore(ignore || ignoreComments);

        return this;
    }

    public PositionAwareDefaultCallback<T, I> enableLineComment(String commentStart) {
        return enableLineComment(commentStart, false);
    }

    public PositionAwareDefaultCallback<T, I> enableLineComment(String commentStart, boolean ignoreCase) {
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
                        return contructComment(start, end, buffer, offset, length);
                    }
                };
            });
        }
        lcacs.setCommentStart(commentStart);
        lcacs.setIgnoreCase(ignoreCase);
        lcacs.setIgnore(ignore || ignoreComments);
        return this;
    }

    public PositionAwareDefaultCallback<T, I> enableLineComment(int... possibleCommentChars) {
        return enableLineComment(ConfCharPredicate.ofChars(possibleCommentChars));
    }

    public PositionAwareDefaultCallback<T, I> enableLineComment(IntPredicate charPredicate) {
        PositionAwareDefaultCallback<T, I> me = this;
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
                        return contructComment(start, end, buffer, offset, length);
                    }
                };
            });
        }
        lcac.setCommentStart(charPredicate);
        lcac.setIgnore(ignore || ignoreComments);
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
