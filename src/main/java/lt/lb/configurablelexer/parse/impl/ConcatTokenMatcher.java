package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class ConcatTokenMatcher<T extends ConfToken> extends CompositeTokenMatcher<T> {

    public ConcatTokenMatcher(String name, TokenMatcher... matchers) {
        super(sumLength(matchers), name, matchers);
    }

    @Override
    public boolean matches(int position, ConfToken token) {
        int i = 0;
        for (TokenMatcher m : matchers) {
            int len = m.getLength();
            if (position >= len) {
                position -= len;
                i++;
            } else {
                return matchers[i].matches(position, token);
            }

        }
        return false;
    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        int i = 0;
        for (TokenMatcher m : matchers) {
            int len = m.getLength();
            if (position >= len) {
                position -= len;
                i++;
            } else {
                return matchers[i].requiredType(position);
            }

        }
        return super.requiredType(position);
    }

}
