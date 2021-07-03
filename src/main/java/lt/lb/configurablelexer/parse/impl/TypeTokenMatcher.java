package lt.lb.configurablelexer.parse.impl;

import java.util.Objects;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class TypeTokenMatcher extends BaseTokenMatcher {

    protected Class<? extends ConfToken> type;

    public TypeTokenMatcher(String name, Class<? extends ConfToken> type) {
        super(1, name);
        this.type = Objects.requireNonNull(type, "Type should not be null");
    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return type;
    }

    @Override
    public boolean matches(int position, ConfToken token) {
        return type.isInstance(token);

    }

}
