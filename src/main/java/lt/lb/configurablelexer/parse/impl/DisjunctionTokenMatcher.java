package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class DisjunctionTokenMatcher<T extends ConfToken> extends CompositeTokenMatcher<T> {

    protected Class<? extends ConfToken>[] minTypes;

    public DisjunctionTokenMatcher(String name, TokenMatcher... matchers) {
        super(assertSameLength(matchers), name, matchers);
        minTypes = new Class[length];

        if (length > 0) {
            for (int pos = 0; pos < length; pos++) {
                Class<? extends ConfToken> requiredType = matchers[0].requiredType(pos);
                if (!ConfToken.class.isAssignableFrom(requiredType)) {
                    throw new IllegalArgumentException(matchers[0].name() + " returns type that is not a subtype of " + ConfToken.class);
                }
                minTypes[pos] = matchers[0].requiredType(pos);

                for (int i = 1; i < matchers.length; i++) {
                    Class maybeBroader = matchers[i].requiredType(pos);
                    if (!ConfToken.class.isAssignableFrom(maybeBroader)) {
                        throw new IllegalArgumentException(matchers[i].name() + " returns type that is not a subtype of " + ConfToken.class);
                    }

                    while (!ConfToken.class.isAssignableFrom(maybeBroader)) {
                        boolean greater = typeComparator.compare(minTypes[pos], maybeBroader) > 0;
                        if (greater) {
                            break;
                        } else {
                            maybeBroader = maybeBroader.getSuperclass();
                        }
                    }
                    minTypes[pos] = maybeBroader;
                }
            }

        }

    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return minTypes[position];
    }

    @Override
    public boolean matches(int position, ConfToken token) {
        for (TokenMatcher matcher : matchers) {
            if (matcher.matches(position, token)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isRepeading() {
        for (TokenMatcher matcher : matchers) {
            if (matcher.isRepeading()) {
                return true;
            }
        }
        return false;
    }

}
