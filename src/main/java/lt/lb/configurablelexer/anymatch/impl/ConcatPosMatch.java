package lt.lb.configurablelexer.anymatch.impl;

import java.util.Arrays;
import java.util.List;
import lt.lb.configurablelexer.anymatch.PosMatch;

/**
 *
 * @author laim0nas100
 */
public class ConcatPosMatch<T,M> extends CompositePosMatch<T,M> {

    public ConcatPosMatch(PosMatch<T,M>... matchers) {
        this(Arrays.asList(matchers));
    }

    public ConcatPosMatch(List<? extends PosMatch<T, M>> matchers) {
        super(sumLength(matchers), matchers);
    }
    
    

    @Override
    public boolean matches(int position, T token) {
        int i = 0;
        for (PosMatch<T,M> m : matchers) {
            int len = m.getLength();
            if (position >= len) {
                position -= len;
                i++;
            } else {
                return matchers.get(i).matches(position, token);
            }

        }
        return false;
    }

}
