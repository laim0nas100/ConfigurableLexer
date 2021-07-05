package lt.lb.configurablelexer.lexer.matchers;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import lt.lb.configurablelexer.lexer.Id;

/**
 *
 * @author laim0nas100
 */
public interface StringMatcher extends Id, Predicate<String> {

    public Match match(String str, int offset, int length);
    
    @Override
    public default boolean test(String str){
        return match(str, 0, str == null ? 0 : str.length()).isFull();
    }
    
    public default boolean canBeBreaking(){
        return false;
    }

    public default int minSize() {
        return 1;
    }

    public static class MatcherMatch {

        public static final Comparator<MatcherMatch> cmpMatcherMatch = new Comparator<MatcherMatch>() {
            @Override
            public int compare(MatcherMatch arg0, MatcherMatch arg1) {
                return Match.cmpIndexThenSizePreference.compare(arg0.match, arg1.match);
            }
        };

        public final StringMatcher matcher;
        public final Match match;

        public MatcherMatch(StringMatcher matcher, Match match) {
            this.matcher = matcher;
            this.match = match;
        }
    }

    public static class Match {

        public static final Comparator<Match> cmpIndexThenSizePreference = new Comparator<Match>() {
            @Override
            public int compare(Match m0, Match m1) {
                Objects.requireNonNull(m0, "Null match");
                Objects.requireNonNull(m1, "Null match");

                if (m0.isPostive() && m1.isPostive()) {
                    if (m0.isFull()) {
                        if (m1.isFull()) {
                            if (m0.isBreaking() && m1.isBreaking()) {
                                return 0;
                            }
                            return m0.isBreaking() ? -1 : 1;
                        } else {
                            return -1;
                        }
                    } else {
                        if (m1.isFull()) {
                            return 1;
                        }
                        //must be partial
                        if (m0.isPartial() && m1.isPartial()) {
                            PartialMatch p0 = (PartialMatch) m0;
                            PartialMatch p1 = (PartialMatch) m1;
                            int c = Integer.compare(p0.from, p1.from);
                            if (c == 0) { // same starting index, prefer bigger size then
                                return Integer.compare(p1.size(), p0.size());
                            } else {
                                return c;
                            }

                        } else {
                            //unknown type, best we can do is compare breaking.
                            if (m0.isBreaking()) {
                                return m1.isBreaking() ? 0 : -1;
                            } else {
                                return m1.isBreaking() ? 1 : 0;
                            }
                        }

                    }
                } else {
                    if (m0.isNegative() && m1.isNegative()) {
                        return 0;
                    } else {
                        return m1.isPostive() ? 1 : -1;
                    }
                }
            }
        };

        private static final NoMatch NO_MATCH = new NoMatch();
        private static final FullMatch FULL_MATCH = new FullMatch(false);
        private static final FullMatch FULL_MATCH_BREAK = new FullMatch(true);

        public static NoMatch noMatch() {
            return NO_MATCH;
        }

        public static FullMatch fullMatch() {
            return FULL_MATCH;
        }

        public static FullMatch fullMatchBreak() {
            return FULL_MATCH_BREAK;
        }

        public static PartialMatch match(int from, int to) {
            return new PartialMatch(from, to, false);
        }

        public static PartialMatch matchBreak(int from, int to) {
            return new PartialMatch(from, to, true);
        }

        public boolean isFull() {
            return this instanceof FullMatch;
        }

        public boolean isPostive() {
            return this instanceof PositiveMatch;
        }

        public boolean isNegative() {
            return !isPostive();
        }

        public boolean isNo() {
            return this instanceof NoMatch;
        }

        public boolean isPartial() {
            return this instanceof PartialMatch;
        }
        
        public boolean isBreaking() {
            return false;
        }

    }

    public static class NoMatch extends Match {

        private NoMatch() {
        }

    }

    public static class PositiveMatch extends Match {

        public final boolean breaking;

        public PositiveMatch(boolean breaking) {
            this.breaking = breaking;
        }

        @Override
        public boolean isBreaking() {
            return breaking;
        }
    }

    public static class FullMatch extends PositiveMatch {

        public FullMatch(boolean breaking) {
            super(breaking);
        }

    }

    public static class PartialMatch extends PositiveMatch {

        public final int from;
        public final int to;

        public PartialMatch(int from, int to, boolean breaking) {
            super(breaking);
            if (from < 0 || to < 0 || from > to) {
                throw new IllegalArgumentException("Illegal range specified: [" + from + " ," + to + ")");
            }
            this.from = from;
            this.to = to;
        }

        public int size() {
            return to - from;
        }

    }

}
