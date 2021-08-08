package lt.lb.configurablelexer.anymatch.impl;

import java.util.Arrays;
import java.util.List;
import lt.lb.configurablelexer.anymatch.PosMatch;

/**
 *
 * @author laim0nas100
 */
public class ConjuctionPosMatch<T, M> extends CompositePosMatch<T, M> {

    public ConjuctionPosMatch(PosMatch<T, M>... matchers) {
        this(Arrays.asList(matchers));
    }
    
    public ConjuctionPosMatch(List<PosMatch<T, M>> matchers) {
        super(assertSameLength(matchers), matchers);
    }

    @Override
    public boolean matches(int position, T token) {
        for (PosMatch<T, M> matcher : matchers) {
            if (!matcher.matches(position, token)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean isRepeating() {
        for (PosMatch<T, M> matcher : matchers) {
            if (!matcher.isRepeating()) {
                return false;
            }
        }
        return true;
    }

}
