package lt.lb.configurablelexer.token;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author laim0nas100
 */
public interface ConfTokenBuffer<T extends ConfToken> {

    public T get(int index);

    public int size();

    public default T getFirst() {
        return get(0);
    }

    public default T getLast() {
        return get(size() - 1);
    }

    public static <T extends ConfToken> ConfTokenBuffer<T> ofList(List<T> list) {
        return new ConfTokenBuffer<T>() {
            @Override
            public T get(int index) {
                return list.get(index);
            }

            @Override
            public int size() {
                return list.size();
            }

            @Override
            public String toString() {
                return list.toString();
            }
            
            
        };
    }

    public static <T extends ConfToken> ConfTokenBuffer<T> of(T... list) {
        return new ConfTokenBuffer<T>() {
            @Override
            public T get(int index) {
                return list[index];
            }

            @Override
            public int size() {
                return list.length;
            }

            @Override
            public String toString() {
                return Arrays.toString(list);
            }
            
        };
    }

}
