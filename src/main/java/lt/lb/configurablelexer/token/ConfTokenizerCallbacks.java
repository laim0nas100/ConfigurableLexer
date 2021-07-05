package lt.lb.configurablelexer.token;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntPredicate;

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

    protected boolean withinNestedCall = false;

    @Override
    public void reset() {
        if (!withinNestedCall && nestedWithin != null) {
            withinNestedCall = true;
            try {
                nestedWithin.reset();
            } finally {
                withinNestedCall = false;
            }
        } else {
            TokenizerCallbacksListeners.super.reset();
        }
    }

    @Override
    public void charListener(boolean isTokenChar, boolean isBreakChar, int c) {
        if (!withinNestedCall && nestedWithin != null) {
            withinNestedCall = true;
            try {
                nestedWithin.charListener(isTokenChar, isBreakChar, c);
            } finally {
                withinNestedCall = false;
            }
        } else {
            TokenizerCallbacksListeners.super.charListener(isTokenChar, isBreakChar, c);
        }
    }

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        if (!withinNestedCall && nestedWithin != null) {
            withinNestedCall = true;
            try {
                return nestedWithin.constructTokens(buffer, offset, length);
            } finally {
                withinNestedCall = false;
            }
        }
        return constructor.constructTokens(buffer, offset, length);
    }

    @Override
    public boolean isTokenChar(int c) {
        if (!withinNestedCall && nestedWithin != null) {
            withinNestedCall = true;
            try {
                return nestedWithin.isTokenChar(c);
            } finally {
                withinNestedCall = false;
            }

        }
        return tokenCharPredicate.test(c);
    }

    @Override
    public boolean isBreakChar(boolean isTokenChar, int c) {
        if (!withinNestedCall && nestedWithin != null) {
            withinNestedCall = true;
            try {
                return nestedWithin.isBreakChar(isTokenChar, c);
            } finally {
                withinNestedCall = false;
            }
        }
        return breakCharPredicate.test(c);
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

    public TokenizerCallbacks<T> nest(Function<TokenizerCallbacks<T>, TokenizerCallbacks<T>> function) {
        if (nestedWithin == null) {
            TokenizerCallbacks<T> apply = function.apply(this);
            setNestedWithin(apply);
            return apply;
        } else {
            TokenizerCallbacks<T> apply = function.apply(nestedWithin);
            setNestedWithin(apply);
            return apply;
        }
    }

}
