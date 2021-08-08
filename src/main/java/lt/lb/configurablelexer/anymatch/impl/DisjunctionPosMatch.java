package lt.lb.configurablelexer.anymatch.impl;

import java.util.Arrays;
import java.util.List;
import lt.lb.configurablelexer.anymatch.PosMatch;

/**
 *
 * @author laim0nas100
 */
public class DisjunctionPosMatch<T, M> extends CompositePosMatch<T, M> {

    public DisjunctionPosMatch(PosMatch<T, M>... matchers) {
        this(Arrays.asList(matchers));
    }

    public DisjunctionPosMatch(List<? extends PosMatch<T, M>> matchers) {
        super(assertSameLength(matchers), matchers);
    }
    

    @Override
    public boolean matches(int position, T token) {
        for (PosMatch<T, M> matcher : matchers) {
            if (matcher.matches(position, token)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRepeating() {
        for (PosMatch<T, M> matcher : matchers) {
            if (matcher.isRepeating()) {
                return true;
            }
        }
        return false;
    }

}
