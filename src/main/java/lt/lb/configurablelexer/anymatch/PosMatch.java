package lt.lb.configurablelexer.anymatch;

import lt.lb.configurablelexer.Id;

/**
 *
 * @author laim0nas100
 * @param <T> item type
 * @param <M> implementation
 */
public interface PosMatch<T, M> extends Id {

    public abstract M getName();

    /**
     * How many items are required. 0 or below means it is never used.
     *
     * @return
     */
    public abstract int getLength();

    /**
     * If the sequence can be repeating.
     *
     * @return
     */
    public abstract boolean isRepeating();

    /**
     * Higher importance means it is tried applied sooner
     *
     * @return
     */
    public abstract int getImportance();

    /**
     * If given item can be matched at given position
     *
     * @param position should be within length (non-negative)
     * @param item
     * @return
     */
    public abstract boolean matches(int position, T item);
    
}
