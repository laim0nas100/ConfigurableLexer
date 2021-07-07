package lt.lb.configurablelexer.lexer.matchers;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.Predicate;
import lt.lb.configurablelexer.Id;

/**
 *
 * @author laim0nas100
 */
public interface StringMatcher extends Id, Predicate<String> {

    public Match match(String str, int offset, int length);

    @Override
    public default boolean test(String str) {
        return match(str, 0, str == null ? 0 : str.length()).isFull();
    }

    public default boolean canBeBreaking() {
        return false;
    }

    public default int minSize() {
        return 1;
    }

    public static class MatcherMatch implements Id {

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

        @Override
        public String stringValues() {
            return "matcher=" + matcher + ", match=" + match;
        }

        @Override
        public String toString() {
            return descriptiveString();
        }

    }

}
