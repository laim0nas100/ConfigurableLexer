package lt.lb.configurablelexer.lexer;

/**
 *
 * @author laim0nas100
 */
public interface StringMatcher extends Id {

    public Match match(String str, int offset, int length);

    public static class Match {

        private static final NoMatch NO_MATCH = new NoMatch();
        private static final FullMatch FULL_MATCH = new FullMatch();

        public static NoMatch noMatch() {
            return NO_MATCH;
        }

        public static FullMatch fullMatch() {
            return FULL_MATCH;
        }

        public static PartialMatch match(int from, int to) {
            return new PartialMatch(from, to);
        }

        public boolean isFull() {
            return this == FULL_MATCH;
        }

        public boolean isNo() {
            return this == NO_MATCH;
        }

        public boolean isPartial() {
            return this instanceof PartialMatch;
        }

    }

    public static class NoMatch extends Match {

        private NoMatch() {
        }

    }

    public static class FullMatch extends Match {

        private FullMatch() {
        }

    }

    public static class PartialMatch extends Match {

        public final int from;
        public final int to;

        private PartialMatch(int from, int to) {
            this.from = from;
            this.to = to;
        }

    }

}
