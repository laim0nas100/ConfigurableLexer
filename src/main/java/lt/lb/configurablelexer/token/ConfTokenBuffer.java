package lt.lb.configurablelexer.token;

import java.util.List;

/**
 *
 * @author laim0nas100
 */
public interface ConfTokenBuffer<T extends ConfToken> {

    public T get(int index);

    public int size();

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
        };
    }
    
    
}
