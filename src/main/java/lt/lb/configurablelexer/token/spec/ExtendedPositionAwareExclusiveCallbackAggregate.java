package lt.lb.configurablelexer.token.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;
import lt.lb.configurablelexer.DisableAware;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.DelegatingTokenizerCallbacks;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 * @param <T>
 * @param <I>
 */
public abstract class ExtendedPositionAwareExclusiveCallbackAggregate<T extends ConfToken, I> implements DelegatingTokenizerCallbacks<T> {

    protected TokenizerCallbacks<T> delegate;
    protected List<ExtendedPositionAwareSplittableCallback<T, I>> callbacks = new ArrayList<>();
    protected TokenizerCallbacks<T> lastDecorated;
    protected boolean ignore;
    protected boolean earlyReturn;
    protected boolean exclusive;

    protected ExtendedPositionAwareSplittableCallback<T, I> exclusiveCallbackRef;

    public ExtendedPositionAwareExclusiveCallbackAggregate(TokenizerCallbacks<T> delegate) {
        this.delegate = delegate;
        this.lastDecorated = delegate;
    }

    public boolean isIgnore() {
        return ignore;
    }

    public void setIgnore(final boolean ignore) {
        this.ignore = ignore;
        callbacks.forEach(call -> call.setIgnore(ignore));
    }

    public boolean isExclusive() {
        return exclusive;
    }

    /**
     * Exclusivity using {@link ExclusivityAware} overrides {@link DisableAware#isDisabled()
     * } property while it is enabled (no super calls to {@link DisableAware#isDisabled()
     * }).
     *
     * @param exclusive
     */
    public void setExclusive(boolean exclusive) {
        this.exclusive = exclusive;
    }

    public boolean isEarlyReturn() {
        return earlyReturn;
    }

    public void setEarlyReturn(boolean earlyReturn) {
        this.earlyReturn = earlyReturn;
        callbacks.forEach(call -> call.setEarlyReturn(earlyReturn));
    }
    
    protected Stream<ExtendedPositionAwareSplittableCallback<T, I>> getCallbackStream(){
        return callbacks.stream().filter(f-> f!= null);
    }

    public abstract I getPosition();

    public ExtendedPositionAwareExclusiveCallbackAggregate<T, I> ignoring(boolean ignoring) {
        setIgnore(ignoring);
        return this;
    }

    /**
     * This is required when you don't want to be matching "string" inside a comment or vice versa.
     * this is not mandatory only when there can be one external state.
     * @param exclusion
     * @return 
     */
    public ExtendedPositionAwareExclusiveCallbackAggregate<T, I> enableExclusion(boolean exclusion) {
        setExclusive(exclusion);
        return this;
    }

    protected void resetAllInternalState() {
        for (ExtendedPositionAwareSplittableCallback<T, I> call : callbacks) {
            call.resetInternalState();
        }
    }

    protected void resetAllInternalStateExceptExclusive() {
        for (ExtendedPositionAwareSplittableCallback<T, I> call : callbacks) {
            if (call != exclusiveCallbackRef) {
                call.resetInternalState();
            }
        }
    }

    @Override
    public TokenizerCallbacks<T> delegate() {
        return lastDecorated;
    }

    public <C extends ExtendedPositionAwareSplittableCallback<T, I>> C addNested(BiFunction<ExclusivityAware<T, I>, TokenizerCallbacks<T>, C> fun) {
        ExclusivityAware<T, I> exclusivityAware = new ExclusivityAware<>(this, null);
        C apply = fun.apply(exclusivityAware, lastDecorated);
        exclusivityAware.setDelegate(apply);

        decorate(apply);
        lastDecorated = exclusivityAware;
        callbacks.add(exclusivityAware);

        return apply;
    }

    protected void decorate(ExtendedPositionAwareSplittableCallback cb) {
        cb.setEarlyReturn(earlyReturn);
        cb.setIgnore(ignore);
    }

    public static class ExclusivityAware<T extends ConfToken, I> implements ExtendedPositionAwareSplittableCallback<T, I> {

        protected ExtendedPositionAwareExclusiveCallbackAggregate<T, I> main;
        protected ExtendedPositionAwareSplittableCallback<T, I> delegate;

        public ExclusivityAware(ExtendedPositionAwareExclusiveCallbackAggregate<T, I> main, ExtendedPositionAwareSplittableCallback<T, I> delegate) {
            this.main = main;
            this.delegate = delegate;
        }

        @Override
        public boolean isDisabled() {
            if (main.isExclusive()) {
                ExtendedPositionAwareSplittableCallback<T, I> ref = main.exclusiveCallbackRef;
                boolean isMe = ref == this;
                return (main.exclusiveCallbackRef != null && !isMe);
            }
            return delegate().isDisabled();

        }

        @Override
        public I start() {
            if (main.isExclusive()) {
                main.exclusiveCallbackRef = this;
                main.resetAllInternalStateExceptExclusive();
            }
            return main.getPosition();
        }

        @Override
        public I mid() {
            return main.getPosition();
        }

        @Override
        public I end() {
            if (main.isExclusive()) {
                main.exclusiveCallbackRef = null;
            }
            return main.getPosition();
        }

        @Override
        public T construct(I start, I end, char[] buffer, int offset, int length) throws Exception {
            return delegate.construct(start, end, buffer, offset, length);
        }

        @Override
        public void resetInternalState() {
            delegate.resetInternalState();
        }

        @Override
        public boolean isWithin() {
            return delegate.isWithin();
        }

        @Override
        public boolean isIgnore() {
            return delegate.isIgnore();
        }

        @Override
        public boolean isEarlyReturn() {
            return delegate.isEarlyReturn();
        }

        @Override
        public TokenizerCallbacks<T> delegate() {
            return delegate;
        }

        @Override
        public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
            return delegate.constructTokens(buffer, offset, length);
        }

        public void setDelegate(ExtendedPositionAwareSplittableCallback<T, I> delegate) {
            this.delegate = delegate;
        }

        public ExtendedPositionAwareSplittableCallback<T, I> getDelegate() {
            return delegate;
        }

        public ExtendedPositionAwareExclusiveCallbackAggregate<T, I> getMain() {
            return main;
        }

        public void setMain(ExtendedPositionAwareExclusiveCallbackAggregate<T, I> main) {
            this.main = main;
        }

        @Override
        public void setIgnore(boolean bool) {
            delegate.setIgnore(bool);
        }

        @Override
        public void setEarlyReturn(boolean bool) {
            delegate.setEarlyReturn(bool);
        }

    }

}
