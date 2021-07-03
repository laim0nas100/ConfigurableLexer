package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class ConjuctionTokenMatcher extends CompositeTokenMatcher {

    protected Class<? extends ConfToken>[] maxTypes;

    public ConjuctionTokenMatcher(String name, TokenMatcher... matchers) {
        super(assertSameLength(matchers), name, matchers);

        maxTypes = new Class[length];
        if (length > 0) {
            for (int pos = 0; pos < length; pos++) {
                maxTypes[pos] = matchers[0].requiredType(pos);

                for (int i = 1; i < matchers.length; i++) {
                    Class<? extends ConfToken> candidate = matchers[i].requiredType(pos);
                    if(typeComparator.compare(maxTypes[pos], candidate) > 0){
                        maxTypes[pos] = candidate;
                    }
                    
                }
            }

        }

    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return maxTypes[position];
    }

    @Override
    public boolean matches(int position, ConfToken token) {
        for (TokenMatcher matcher : matchers) {
            if (!matcher.matches(position, token)) {
                return false;
            }
        }
        return true;
    }

}
