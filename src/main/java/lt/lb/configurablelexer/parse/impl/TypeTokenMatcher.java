package lt.lb.configurablelexer.parse.impl;

import java.util.Objects;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class TypeTokenMatcher<T extends ConfToken> extends BaseTokenMatcher<T> {

    protected Class<? extends ConfToken> type;
    protected boolean exact;

    public TypeTokenMatcher(String name, Class<? extends ConfToken> type) {
        this(name, type, false);
    }

    public TypeTokenMatcher(String name, Class<? extends ConfToken> type, boolean exact) {
        super(1, name);
        this.type = Objects.requireNonNull(type, "Type should not be null");
        this.exact = exact;
    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return type;
    }

    @Override
    public boolean matches(int position, T token) {
        return exact ? type.equals(token.getClass()) : type.isInstance(token);

    }

}
