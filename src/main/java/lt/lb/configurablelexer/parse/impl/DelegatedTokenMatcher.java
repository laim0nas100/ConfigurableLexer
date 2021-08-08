package lt.lb.configurablelexer.parse.impl;

import java.util.Objects;
import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class DelegatedTokenMatcher<T extends ConfToken> implements TokenMatcher<T> {

    protected TokenMatcher delegate;

    public DelegatedTokenMatcher(TokenMatcher delegate) {
        this.delegate = Objects.requireNonNull(delegate, "Delegated matcher was not supplied");
    }

    public TokenMatcher getDelegate() {
        return delegate;
    }

    @Override
    public String name() {
        return delegate.name();
    }

    @Override
    public int getLength() {
        return delegate.getLength();
    }

    @Override
    public int getImportance() {
        return delegate.getImportance();
    }

    @Override
    public boolean isRepeating() {
        return delegate.isRepeating();
    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return delegate.requiredType(position);
    }

    @Override
    public boolean matches(int position, ConfToken token) {
        return delegate.matches(position, token);
    }

    @Override
    public String toString() {
        return delegate.toString();
    }

}
