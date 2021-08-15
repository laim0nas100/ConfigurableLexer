package lt.lb.configurablelexer.anymatch.impl;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lt.lb.configurablelexer.anymatch.PosMatch;
import lt.lb.configurablelexer.anymatch.PosMatched;

/**
 *
 * @author laim0nas100
 * @param <T> type of items to be matched
 * @param <I> matcher id type
 * @param <P> base PosMatch type
 * @param <PP> base PosMatch lifted type
 * @param <M> Matchers implementation
 *
 *
 */
public abstract class Matchers<T, I, P extends PosMatch<T, I>, PP extends PosMatch<PosMatched<T, I>, I>, M extends Matchers<T, I, P, PP, M>> {

    protected I name;
    protected boolean repeating;
    protected int importance;

    public static class SimpleMatchers<T, I> extends Matchers<T, I, PosMatch<T, I>, PosMatch<PosMatched<T, I>, I>, SimpleMatchers<T, I>> {

        @Override
        protected PosMatch<T, I> simpleType(PosMatch<T, I> posMatched) {
            return posMatched;
        }

        @Override
        protected PosMatch<PosMatched<T, I>, I> liftedType(PosMatch<PosMatched<T, I>, I> posMatchedLift) {
            return posMatchedLift;
        }

        @Override
        protected SimpleMatchers<T, I> create() {
            return new SimpleMatchers<>();
        }

        @Override
        protected SimpleMatchers<T, I> me() {
            return this;
        }

    }

    public static <T, I> SimpleMatchers<T, I> simple() {
        return new SimpleMatchers<>();
    }

    public M makeNew(I name) {
        Objects.requireNonNull(name);
        M matchers = this.create();
        matchers.name = name;
        matchers.importance = importance;
        matchers.repeating = repeating;
        return matchers;
    }

    public M setDefaultName(I name) {
        M me = me();
        me.name = name;
        return me;
    }

    public M repeating(boolean repeating) {
        M me = me();
        me.repeating = repeating;
        return me;
    }

    public M importance(int importance) {
        M me = me();
        me.importance = importance;
        return me;
    }

    public P isWhen(Predicate<T> pred) {
        return decorate(new PredicatePosMatch<>(pred));
    }

    public P isNotWhen(Predicate<T> pred) {
        Objects.requireNonNull(pred);
        return decorate(new PredicatePosMatch<>(pred.negate()));
    }

    public P isEqual(T other) {
        return decorate(new PredicatePosMatch<>(t -> Objects.equals(t, other)));
    }

    public P isNotEqual(T other) {
        return decorate(new PredicatePosMatch<>(t -> !Objects.equals(t, other)));
    }

    public P ofType(Class<? extends T> cls, final boolean exact) {
        Objects.requireNonNull(cls);
        return decorate(new PredicatePosMatch<>(t -> {
            if (exact) {
                if (t == null) {
                    return false;
                }
                return cls.getClass().equals(t.getClass());
            }
            return cls.isInstance(t);
        }));
    }

    public P ofType(Class<? extends T> cls) {
        return ofType(cls, false);
    }

    public P any(int len) {
        return decorate(new AnyPosMatch<>(len));
    }

    public PP anyLifted(int len) {
        return decorateLifted(new AnyPosMatch<>(len));
    }

    public P or(PosMatch<T, I>... matchers) {
        return decorate(new DisjunctionPosMatch<>(matchers));
    }

    public PP orLifted(PosMatch<PosMatched<T, I>, I>... matchers) {
        return decorateLifted(new DisjunctionPosMatch<>(matchers));
    }

    public P and(PosMatch<T, I>... matchers) {
        return decorate(new ConjuctionPosMatch<>(matchers));
    }

    public PP andLifted(PosMatch<PosMatched<T, I>, I>... matchers) {
        return decorateLifted(new ConjuctionPosMatch<>(matchers));
    }

    public P concat(PosMatch<T, I>... matchers) {
        return decorate(new ConcatPosMatch<>(matchers));
    }

    public PP concatLifted(PosMatch<PosMatched<T, I>, I>... matchers) {
        return decorateLifted(new ConcatPosMatch<>(matchers));
    }

    public PP concatLifted(I... names) {
        List<NameLiftPosMatch<T, I>> collect = Stream.of(names).map(n -> new NameLiftPosMatch<T, I>(n)).collect(Collectors.toList());
        return decorateLifted(new ConcatPosMatch<>(collect));
    }

    public PP concatLiftedNames(PosMatch<T, I>... matchers) {
        List<NameLiftPosMatch<T, I>> collect = Stream.of(matchers).map(n -> new NameLiftPosMatch<T, I>(n.getName())).collect(Collectors.toList());
        return decorateLifted(new ConcatPosMatch<>(collect));
    }

    public PP lifted(I name) {
        return decorateLifted(new NameLiftPosMatch<>(name));
    }

    public PP lifted(PosMatch<T, I> posMatch) {
        return decorateLifted(new NameLiftPosMatch<>(posMatch.getName()));
    }

    protected <K extends BasePosMatch<T, I>> P decorate(K k) {
        k.importance = importance;
        k.name = name;
        k.repeating = repeating;
        return simpleType(k);
    }

    protected <K extends BasePosMatch<PosMatched<T, I>, I>> PP decorateLifted(K k) {
        k.importance = importance;
        k.name = name;
        k.repeating = repeating;
        return liftedType(k);
    }

    protected abstract P simpleType(PosMatch<T, I> posMatched);

    protected abstract PP liftedType(PosMatch<PosMatched<T, I>, I> posMatchedLift);

    protected abstract M create();

    protected abstract M me();

}
