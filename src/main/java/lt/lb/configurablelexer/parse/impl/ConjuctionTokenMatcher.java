package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class ConjuctionTokenMatcher<T extends ConfToken> extends CompositeTokenMatcher<T> {

    protected Class<? extends ConfToken>[] narrowTypes;

    public ConjuctionTokenMatcher(String name, TokenMatcher... matchers) {
        super(assertSameLength(matchers), name, matchers);

        narrowTypes = new Class[length];
        if (length > 0) {
            for (int pos = 0; pos < length; pos++) {
                narrowTypes[pos] = matchers[0].requiredType(pos);

                for (int i = 1; i < matchers.length; i++) {
                    Class<? extends ConfToken> maybeNarrow = matchers[i].requiredType(pos);
                    if (typeComparator.compare(narrowTypes[pos], maybeNarrow) < 0) {
                        narrowTypes[pos] = maybeNarrow;
                    }

                }
            }

        }

    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return narrowTypes[position];
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

    @Override
    public boolean isRepeading() {
        for (TokenMatcher matcher : matchers) {
            if (!matcher.isRepeading()) {
                return false;
            }
        }
        return true;
    }

}
