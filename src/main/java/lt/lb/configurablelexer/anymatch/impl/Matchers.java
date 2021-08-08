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
 */
public class Matchers<T, I> {

    protected I name;
    protected boolean repeating;
    protected int importance;

    public Matchers<T, I> makeNew(I name) {
        Objects.requireNonNull(name);
        Matchers<T, I> matchers = new Matchers<>();
        matchers.name = name;
        matchers.importance = importance;
        matchers.repeating = repeating;
        return matchers;
    }

    public Matchers<T, I> setDefaultName(I name) {
        this.name = name;
        return this;
    }

    public Matchers<T, I> repeating(boolean repeating) {
        this.repeating = repeating;
        return this;
    }

    public Matchers<T, I> importance(int importance) {
        this.importance = importance;
        return this;
    }

    public PredicatePosMatch<T, I> isWhen(Predicate<T> pred) {
        return decorate(new PredicatePosMatch<>(pred));
    }

    public PredicatePosMatch<T, I> isNotWhen(Predicate<T> pred) {
        Objects.requireNonNull(pred);
        return decorate(new PredicatePosMatch<>(pred.negate()));
    }

    public PredicatePosMatch<T, I> isEqual(T other) {
        return decorate(new PredicatePosMatch<>(t -> Objects.equals(t, other)));
    }

    public PredicatePosMatch<T, I> isNotEqual(T other) {
        return decorate(new PredicatePosMatch<>(t -> !Objects.equals(t, other)));
    }

    public PredicatePosMatch<T, I> ofType(Class<? extends T> cls, final boolean exact) {
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

    public PredicatePosMatch<T, I> ofType(Class<? extends T> cls) {
        return ofType(cls, false);
    }

    public AnyPosMatch<T, I> any(int len) {
        return decorate(new AnyPosMatch<>(len));
    }

    public AnyPosMatch<PosMatched<T, I>, I> anyLifted(int len) {
        return decorateLifted(new AnyPosMatch<>(len));
    }

    public DisjunctionPosMatch<T, I> or(PosMatch<T, I>... matchers) {
        return decorate(new DisjunctionPosMatch<>(matchers));
    }

    public DisjunctionPosMatch<PosMatched<T, I>, I> orLifted(PosMatch<PosMatched<T, I>, I>... matchers) {
        return decorateLifted(new DisjunctionPosMatch<>(matchers));
    }

    public ConjuctionPosMatch<T, I> and(PosMatch<T, I>... matchers) {
        return decorate(new ConjuctionPosMatch<>(matchers));
    }

    public ConjuctionPosMatch<PosMatched<T, I>, I> andLifted(PosMatch<PosMatched<T, I>, I>... matchers) {
        return decorateLifted(new ConjuctionPosMatch<>(matchers));
    }

    public ConcatPosMatch<T, I> concat(PosMatch<T, I>... matchers) {
        return decorate(new ConcatPosMatch<>(matchers));
    }

    public ConcatPosMatch<PosMatched<T, I>, I> concatLifted(PosMatch<PosMatched<T, I>, I>... matchers) {
        return decorateLifted(new ConcatPosMatch<>(matchers));
    }

    public ConcatPosMatch<PosMatched<T, I>, I> concatLifted(I... names) {
        List<NameLiftPosMatch<T, I>> collect = Stream.of(names).map(n -> new NameLiftPosMatch<T, I>(n)).collect(Collectors.toList());
        return decorateLifted(new ConcatPosMatch<>(collect));
    }

    public ConcatPosMatch<PosMatched<T, I>, I> concatLiftedNames(PosMatch<T, I>... matchers) {
        List<NameLiftPosMatch<T, I>> collect = Stream.of(matchers).map(n -> new NameLiftPosMatch<T, I>(n.getName())).collect(Collectors.toList());
        return decorateLifted(new ConcatPosMatch<>(collect));
    }

    public NameLiftPosMatch<T, I> lifted(I name) {
        return decorateLifted(new NameLiftPosMatch<>(name));
    }

    public NameLiftPosMatch<T, I> lifted(PosMatch<T, I> posMatch) {
        return decorateLifted(new NameLiftPosMatch<>(posMatch.getName()));
    }

    protected <K extends BasePosMatch<T, I>> K decorate(K k) {
        k.importance = importance;
        k.name = name;
        k.repeating = repeating;
        return k;
    }

    protected <K extends BasePosMatch<PosMatched<T, I>, I>> K decorateLifted(K k) {
        k.importance = importance;
        k.name = name;
        k.repeating = repeating;
        return k;
    }

}
