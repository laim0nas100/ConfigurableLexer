package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.parse.TokenMatcher;


/**
 *
 * @author laim0nas100
 */
public class ImportanceTokenMatcher extends DelegatedTokenMatcher {

    protected int importance;

    public ImportanceTokenMatcher(int importance, TokenMatcher delegate) {
        super(delegate);
        this.importance = importance;
    }

    @Override
    public int importance() {
        return importance;
    }

}
