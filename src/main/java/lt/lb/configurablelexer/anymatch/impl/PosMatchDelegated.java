package lt.lb.configurablelexer.anymatch.impl;

import lt.lb.configurablelexer.anymatch.PosMatch;

/**
 *
 * @author laim0nas100
 */
public interface PosMatchDelegated<T, I> extends PosMatch<T, I> {

    public PosMatch<T, I> delegate();

    @Override
    public default I getName() {
        return delegate().getName();
    }

    @Override
    public default int getLength() {
        return delegate().getLength();
    }

    @Override
    public default boolean isRepeating() {
        return delegate().isRepeating();
    }

    @Override
    public default int getImportance() {
        return delegate().getImportance();
    }

    @Override
    public default boolean matches(int position, T item) {
        return delegate().matches(position, item);
    }

    @Override
    public default Object id() {
        return delegate().id();
    }

    @Override
    public default String stringValues() {
        return delegate().stringValues();
    }

    @Override
    public default String descriptiveString() {
        return delegate().descriptiveString();
    }

}
