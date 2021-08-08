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
    
    public default PosMatch<PosMatched<T,M>,M> lifted(){
        PosMatch<T, M> me = this;
        return new PosMatch<PosMatched<T, M>, M>(){
            @Override
            public M getName() {
                return me.getName();
            }

            @Override
            public int getLength() {
                return me.getLength();
            }

            @Override
            public boolean isRepeating() {
                return me.isRepeating();
            }

            @Override
            public int getImportance() {
                return me.getImportance();
            }

            @Override
            public boolean matches(int position, PosMatched<T, M> item) {
                return item.matchedBy.indexOf(me.getName()) == position;
            }
        };
    }

}
