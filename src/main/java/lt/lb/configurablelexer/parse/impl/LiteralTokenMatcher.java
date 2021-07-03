package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.base.LiteralToken;

/**
 *
 * @author laim0nas100
 */
public class LiteralTokenMatcher extends ExactTokenMatcher {

    public LiteralTokenMatcher(boolean ignoreCase, String name, String word) {
        super(ignoreCase, name, word);
    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return LiteralToken.class;
    }

}
