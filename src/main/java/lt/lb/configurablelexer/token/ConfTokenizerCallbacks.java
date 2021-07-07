package lt.lb.configurablelexer.token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntPredicate;
import lt.lb.configurablelexer.Redirecter;

/**
 *
 * @author laim0nas100
 *
 * This acts as a starting and stopping point for all
 * {@link TokenizerCallbacks}.
 *
 * This can be nested within something else, then the callback starts here but
 * is redirected to where is it nested then assuming every implementation is
 * correct (call super or delegate on appropriate methods), it can trickle back
 * down here.
 *
 */
public class ConfTokenizerCallbacks<T extends ConfToken> implements TokenizerCallbacksListeners<T> {

    protected List<CharListener> charListeners = new ArrayList<>();
    protected IntPredicate tokenCharPredicate = c -> true;
    protected IntPredicate breakCharPredicate = c -> false;
    protected ConfTokenConstructor<T> constructor = ConfTokenConstructor.NOT_SET;

    protected TokenizerCallbacks<T> nestedWithin;

    public ConfTokenizerCallbacks() {
        nestedWithin = this;
    }

    protected final Redirecter.RedirecterRun redirReset = Redirecter.RedirecterRun.of(() -> getNestedWithin().reset(), TokenizerCallbacksListeners.super::reset);

    @Override
    public void reset() {
        redirReset.run();
    }

    protected final Redirecter.RedirecterCharListener redirCharListener = new Redirecter.RedirecterCharListener(
            (ch, chr) -> getNestedWithin().charListener(ch, chr), TokenizerCallbacksListeners.super::charListener);

    @Override
    public void charListener(CharInfo chInfo, int c) {
        redirCharListener.charListener(chInfo, c);
    }

    protected final Redirecter.RedirecterConfTokenConstructor<T> redirConstructor
            = new Redirecter.RedirecterConfTokenConstructor((b,o,l) -> getNestedWithin().constructTokens(b, o, l), (b,o,l) -> constructor.constructTokens(b, o, l));
    
    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return redirConstructor.constructTokens(buffer, offset, length);
    }

    protected final Redirecter.RedirecterIntPredicate redirIsToken
            = new Redirecter.RedirecterIntPredicate(c -> getNestedWithin().isTokenChar(c), c -> tokenCharPredicate.test(c));

    @Override
    public boolean isTokenChar(int c) {
        return redirIsToken.test(c);
    }

    protected final Redirecter.RedirecterIntPredicate redirIsBreak
            = new Redirecter.RedirecterIntPredicate(c -> getNestedWithin().isBreakChar(c), c -> breakCharPredicate.test(c));

    @Override
    public boolean isBreakChar(int c) {
        return redirIsBreak.test(c);
    }

    public ConfTokenizerCallbacks<T> addListener(CharListener listener) {
        charListeners.add(listener);
        return this;
    }

    @Override
    public Iterable<CharListener> listeners() {
        return charListeners;
    }

    public List<CharListener> getCharListeners() {
        return charListeners;
    }

    public void setCharListeners(List<CharListener> charListeners) {
        this.charListeners = charListeners;
    }

    public ConfTokenConstructor<T> getConstructor() {
        return constructor;
    }

    public void setConstructor(ConfTokenConstructor<T> constructor) {
        this.constructor = constructor;
    }

    public IntPredicate getTokenCharPredicate() {
        return tokenCharPredicate;
    }

    public ConfTokenizerCallbacks<T> setTokenCharPredicate(IntPredicate tokenCharPredicate) {
        this.tokenCharPredicate = tokenCharPredicate;
        return this;
    }

    public IntPredicate getBreakCharPredicate() {
        return breakCharPredicate;
    }

    public ConfTokenizerCallbacks<T> setBreakCharPredicate(IntPredicate breakCharPredicate) {
        this.breakCharPredicate = breakCharPredicate;
        return this;
    }

    public TokenizerCallbacks<T> getNestedWithin() {
        return nestedWithin;
    }

    public ConfTokenizerCallbacks setNestedWithin(TokenizerCallbacks<T> nestedWithin) {
        this.nestedWithin = nestedWithin;
        return this;
    }

    public <N extends TokenizerCallbacks<T>> N nest(Function<TokenizerCallbacks<T>, N> function) {
        if (nestedWithin == null) {
            N apply = function.apply(this);
            setNestedWithin(apply);
            return apply;
        } else {
            N apply = function.apply(nestedWithin);
            setNestedWithin(apply);
            return apply;
        }
    }

}
