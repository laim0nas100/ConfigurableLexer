package lt.lb.configurablelexer.anymatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lt.lb.configurablelexer.Id;

/**
 *
 * @author laim0nas100
 */
public class PosMatched<T, P> implements Id {

    public final List<P> matchedBy;
    public final List<T> items;

    public PosMatched(List<T> items) {

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Empty items");
        }
        this.matchedBy = Arrays.asList();
        this.items = items;
    }

    public PosMatched(List<P> matched, List<T> items) {

        if (matched == null || matched.isEmpty()) {
            throw new IllegalArgumentException("Empty matched");
        }
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Empty items");
        }
        this.matchedBy = matched;
        this.items = items;
    }

    public P firstMatch() {
        return matchedBy.size() > 0 ? matchedBy.get(0) : null;
    }

    public T getItem(int index) {
        return items.get(index);
    }

    public List<T> getItems(int... index) {
        List<T> list = new ArrayList<>(index.length);
        for (int i = 0; i < index.length; i++) {
            list.add(items.get(index[i]));
        }
        return list;
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

    public int count() {
        return items.size();
    }

    @Override
    public String stringValues() {
        return "matchedBy=" + names() + ", tokens=" + values();
    }

}
