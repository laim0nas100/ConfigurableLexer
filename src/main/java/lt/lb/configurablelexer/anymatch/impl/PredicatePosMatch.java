package lt.lb.configurablelexer.anymatch.impl;

import java.util.Objects;
import java.util.function.Predicate;

/**
 *
 * @author laim0nas100
 */
public class PredicatePosMatch<T,M> extends BasePosMatch<T, M>{

    protected final Predicate<T> pred;
    
    public PredicatePosMatch(Predicate<T> pred) {
        this.pred = Objects.requireNonNull(pred);
        this.length = 1;
    }

    @Override
    public boolean matches(int position, T item) {
        return pred.test(item);
    }
    
}
