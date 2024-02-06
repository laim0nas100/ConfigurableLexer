package lt.lb.configurablelexer.anymatch.impl;

import java.util.Collections;
import java.util.List;
import lt.lb.configurablelexer.Id;
import lt.lb.configurablelexer.anymatch.PosMatched;

/**
 *
 * @author laim0nas100
 */
public class PosMatchedSimple<T, P> implements PosMatched<T, P>, Id {

    private static final PosMatchedSimple empty = new PosMatchedSimple();

    public static <T, P> PosMatchedSimple<T, P> empty() {
        return empty;
    }

    protected final List<P> matchedBy;
    protected final List<T> items;

    private PosMatchedSimple() {
        this.matchedBy = Collections.emptyList();
        this.items = Collections.emptyList();
    }

    public PosMatchedSimple(List<T> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Empty items");
        }
        this.matchedBy = Collections.emptyList();
        this.items = items;
    }

    public PosMatchedSimple(List<P> matched, List<T> items) {

        if (matched == null || matched.isEmpty()) {
            throw new IllegalArgumentException("Empty matched");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Empty items");
        }
        this.matchedBy = matched;
        this.items = items;
    }

    public boolean contains(P matcher) {
        return matchedBy.contains(matcher);
    }

    @Override
    public String toString() {
        return descriptiveString();
    }

    public String names() {
        return "" + matchedBy;
    }

    public String values() {
        return "" + items;
    }

    @Override
    public String stringValues() {
        return "matchedBy=" + names() + ", tokens=" + values();
    }

    @Override
    public List<P> matchedBy() {
        return matchedBy;
    }

    @Override
    public List<T> items() {
        return items;
    }

}
