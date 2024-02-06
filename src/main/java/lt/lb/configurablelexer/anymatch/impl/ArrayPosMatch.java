package lt.lb.configurablelexer.anymatch.impl;

import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class ArrayPosMatch<T, M> extends BasePosMatch<T, M> {

    protected T[] array;

    public ArrayPosMatch(T[] array) {
        this.length = array.length;
        this.array = array;
    }

    @Override
    public boolean matches(int position, T item) {
        return Objects.equals(item, array[position]);
    }
}
