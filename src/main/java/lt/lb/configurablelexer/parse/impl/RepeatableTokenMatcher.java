package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class RepeatableTokenMatcher<T extends ConfToken> extends DelegatedTokenMatcher<T> {

    protected boolean rep;

    public RepeatableTokenMatcher(boolean rep, TokenMatcher delegate) {
        super(delegate);
        this.rep = rep;
    }

    @Override
    public boolean isRepeating() {
        return rep;
    }

}
