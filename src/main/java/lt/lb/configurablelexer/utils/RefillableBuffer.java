package lt.lb.configurablelexer.utils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public class RefillableBuffer<T> implements Iterator<T> {

    protected LinkedList<T> buffer = new LinkedList<>();
    protected Iterator<T> real;

    public RefillableBuffer(Iterator<T> real) {
        this.real = Objects.requireNonNull(real, "Iterator provided is null");
    }

    @Override
    public boolean hasNext() {
        return !buffer.isEmpty() || real.hasNext();
    }

    @Override
    public T next() {
        if (buffer.isEmpty()) {
            return real.next();
        } else {
            return buffer.pollLast();
        }

    }

    public void returnItem(T item) {
        buffer.addLast(item);
    }

}
