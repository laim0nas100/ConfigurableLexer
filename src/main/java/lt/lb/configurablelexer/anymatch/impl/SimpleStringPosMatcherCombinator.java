package lt.lb.configurablelexer.anymatch.impl;

import java.util.Collection;
import java.util.Iterator;
import lt.lb.configurablelexer.anymatch.PosMatch;

/**
 *
 * @author laim0nas100
 */
public class SimpleStringPosMatcherCombinator<T> extends SimplePosMatcherCombinator<T, String, PosMatch<T, String>> {

    public SimpleStringPosMatcherCombinator(Iterator<T> items, Collection<? extends PosMatch<T, String>> matchers) {
        super(items, matchers);
    }
    
}
