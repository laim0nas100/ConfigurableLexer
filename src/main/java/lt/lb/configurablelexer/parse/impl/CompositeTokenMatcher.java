package lt.lb.configurablelexer.parse.impl;

import java.util.Comparator;
import java.util.stream.Stream;
import lt.lb.configurablelexer.parse.TokenMatcher;

/**
 *
 * @author laim0nas100
 */
public abstract class CompositeTokenMatcher extends BaseTokenMatcher {
    
    public static final Comparator<Class> typeComparator = new Comparator<Class>() {
        @Override
        public int compare(Class o1, Class o2) {
            if (o1 == null && o2 == null) {
                return 0;
            }

            if (o1 == null) {
                return -1;
            }
            
            if (o2 == null) {
                return 1;
            }
            
            boolean i_1 = instanceOfClass(o1, o2);
            boolean i_2 = instanceOfClass(o2, o1);
            if (i_1 && !i_2) {
                return 1;
            }
            if (!i_1 && i_2) {
                return -1;
            }

            return 0;
        }

    };
    
    /**
     * Null-friendly {@code Class.isAssignableFrom} version.
     *
     * @param what
     * @param of
     *
     * Static equivalent: what instanceof of
     * @return
     */
    public static boolean instanceOfClass(Class what, Class of) {
        if (of == null) { // nothing except null is instance of null
            return what == null;
        }
        if (what == null) {
            return false;
        } else {
            return of.isAssignableFrom(what);
        }
    }
    

    protected TokenMatcher[] matchers;

    public CompositeTokenMatcher(int length, String name, TokenMatcher... matchers) {
        super(length, name);
        this.matchers = assertArray(matchers);
    }

    public static int sumLength(TokenMatcher... matchers) {
        return Stream.of(matchers).mapToInt(m -> m.length()).sum();
    }

    public static int assertSameLength(TokenMatcher... matchers) {
        final int expectedLength = matchers[0].length();

        for (int i = 1; i < matchers.length; i++) {
            int length = matchers[i].length();
            if (expectedLength != length) {
                throw new IllegalArgumentException("Length mismatch. Expected " + expectedLength + " but found:" + length + " at " + matchers[i].name());
            }
        }
        return expectedLength;
    }

}
