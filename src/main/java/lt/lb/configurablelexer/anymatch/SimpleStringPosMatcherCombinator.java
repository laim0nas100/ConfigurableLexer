package lt.lb.configurablelexer.anymatch;

import java.util.Collection;
import java.util.Iterator;

/**
 *
 * @author laim0nas100
 */
public class SimpleStringPosMatcherCombinator<T> extends SimplePosMatcherCombinator<T, String, PosMatch<T, String>> {

    public SimpleStringPosMatcherCombinator(Iterator<T> items, Collection<? extends PosMatch<T, String>> matchers) {
        super(items, matchers);
    }
    
}
