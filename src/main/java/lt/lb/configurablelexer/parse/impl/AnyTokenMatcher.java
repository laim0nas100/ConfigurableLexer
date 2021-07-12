package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class AnyTokenMatcher<T extends ConfToken> extends BaseTokenMatcher<T> {

    public AnyTokenMatcher(int length, String name) {
        super(length, name);
    }

    @Override
    public boolean matches(int position, ConfToken token) {
        return true;
    }

}
