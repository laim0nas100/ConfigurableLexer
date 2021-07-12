package lt.lb.configurablelexer;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntPredicate;
import java.util.function.Supplier;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.CharListener;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenConstructor;

/**
 *
 * @author laim0nas100
 */
public class Redirecter<WAY> {

    protected final WAY first;
    protected final WAY second;
    protected boolean nested;

    public Redirecter(WAY first, WAY second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    public static <T> RedirecterSupplier<T> of(Supplier<T> first, Supplier<T> second) {
        return new RedirecterSupplier<>(first, second);
    }

    public static <T> RedirecterRun of(Runnable first, Runnable second) {
        return new RedirecterRun(first, second);
    }

    public static class RedirecterSupplier<T> extends Redirecter<Supplier<T>> implements Supplier<T> {

        public RedirecterSupplier(Supplier<T> first, Supplier<T> second) {
            super(first, second);
        }

        @Override
        public T get() {
            if (nested) {
                return second.get();
            }
            try {
                nested = true;
                return first.get();
            } finally {
                nested = false;
            }
        }

    }

    public static class RedirecterFunction<A, B> extends Redirecter<Function<A, B>> implements Function<A, B> {

        public RedirecterFunction(Function<A, B> first, Function<A, B> second) {
            super(first, second);
        }

        @Override
        public B apply(A t) {
            if (nested) {
                return second.apply(t);
            } else {
                try {
                    nested = true;
                    return first.apply(t);
                } finally {
                    nested = false;
                }
            }
        }

    }

    public static class RedirecterBiFunction<A, B, C> extends Redirecter<BiFunction<A, B, C>> implements BiFunction<A, B, C> {

        public RedirecterBiFunction(BiFunction<A, B, C> first, BiFunction<A, B, C> second) {
            super(first, second);
        }

        @Override
        public C apply(A t, B u) {
            if (nested) {
                return second.apply(t, u);
            } else {
                try {
                    nested = true;
                    return first.apply(t, u);
                } finally {
                    nested = false;
                }
            }

        }

    }

    public static class RedirecterBiConsumer<A, B> extends Redirecter<BiConsumer<A, B>> implements BiConsumer<A, B> {

        public RedirecterBiConsumer(BiConsumer<A, B> first, BiConsumer<A, B> second) {
            super(first, second);
        }

        @Override
        public void accept(A t, B u) {
            if (nested) {
                second.accept(t, u);
            } else {
                try {
                    nested = true;
                    first.accept(t, u);
                } finally {
                    nested = false;
                }
            }

        }
    }

    public static class RedirecterConsumer<A> extends Redirecter<Consumer<A>> implements Consumer<A> {

        public RedirecterConsumer(Consumer<A> first, Consumer<A> second) {
            super(first, second);
        }

        @Override
        public void accept(A t) {
            if (nested) {
                second.accept(t);
            } else {
                try {
                    nested = true;
                    first.accept(t);
                } finally {
                    nested = false;
                }
            }

        }
    }

    public static class RedirecterRun extends Redirecter<Runnable> implements Runnable {

        public RedirecterRun(Runnable first, Runnable second) {
            super(first, second);
        }

        @Override
        public void run() {
            if (nested) {
                second.run();
            } else {
                try {
                    nested = true;
                    first.run();
                } finally {
                    nested = false;
                }
            }

        }

    }

    public static class RedirecterCharListener extends Redirecter<CharListener> implements CharListener {

        public RedirecterCharListener(CharListener first, CharListener second) {
            super(first, second);
        }

        @Override
        public void charListener(CharInfo chInfo, int c) {
            if (nested) {
                second.charListener(chInfo, c);
            } else {
                try {
                    nested = true;
                    first.charListener(chInfo, c);
                } finally {
                    nested = false;
                }
            }
        }

    }

    public static class RedirecterConfTokenConstructor<T extends ConfToken> extends Redirecter<ConfTokenConstructor<T>> implements ConfTokenConstructor<T> {

        public RedirecterConfTokenConstructor(ConfTokenConstructor<T> first, ConfTokenConstructor<T> second) {
            super(first, second);
        }

        @Override
        public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
            if (nested) {
                return second.constructTokens(buffer, offset, length);
            } else {
                try {
                    nested = true;
                    return first.constructTokens(buffer, offset, length);
                } finally {
                    nested = false;
                }
            }
        }

    }

    public static class RedirecterIntPredicate extends Redirecter<IntPredicate> implements IntPredicate {

        public RedirecterIntPredicate(IntPredicate first, IntPredicate second) {
            super(first, second);
        }

        @Override
        public boolean test(int value) {
            if (nested) {
                return second.test(value);
            }
            try {
                nested = true;
                return first.test(value);
            } finally {
                nested = false;
            }
        }

    }

    public static class RedirecterDisableAware extends Redirecter<DisableAware> implements DisableAware {

        public RedirecterDisableAware(DisableAware first, DisableAware second) {
            super(first, second);
        }

        @Override
        public boolean isDisabled() {
            if (nested) {
                return second.isDisabled();
            }
            try {
                nested = true;
                return first.isDisabled();
            } finally {
                nested = false;
            }
        }
    }

}
