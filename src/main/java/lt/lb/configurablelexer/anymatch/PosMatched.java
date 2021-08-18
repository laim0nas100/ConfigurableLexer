package lt.lb.configurablelexer.anymatch;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author laim0nas100
 */
public interface PosMatched<T, P> {
    
    public List<P> matchedBy();
    
    public List<T> items();
    
    public default int countMatchers(){
        return matchedBy().size();
    }
    
    public default int countItems(){
        return items().size();
    }

    public default P firstMatch() {
        return countMatchers() > 0 ? matchedBy().get(0) : null;
    }

    public default T getItem(int index) {
        return items().get(index);
    }

    public default List<T> getItems(int... index) {
        List<T> list = new ArrayList<>(index.length);
        for (int i = 0; i < index.length; i++) {
            list.add(getItem(index[i]));
        }
        return list;
    }

    public default boolean containsMatcher(P matcher) {
        return matchedBy().contains(matcher);
    }
    
    public default boolean containsItem(T item) {
        return items().contains(item);
    }
}
