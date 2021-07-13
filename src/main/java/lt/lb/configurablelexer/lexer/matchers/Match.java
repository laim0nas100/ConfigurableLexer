/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lt.lb.configurablelexer.lexer.matchers;

import java.util.Comparator;
import java.util.Objects;
import lt.lb.configurablelexer.Id;

/**
 *
 * @author laim0nas100
 */
public interface Match extends Id {

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
                            c = Integer.compare(p1.size(), p0.size());
                            if (c != 0) {
                                return c;
                            }//else try to compare breaking
                        } else {
                            return c;
                        }

                    }
                    // best we can do is compare breaking.
                    if (m0.isBreaking()) {
                        return m1.isBreaking() ? 0 : -1;
                    } else {
                        return m1.isBreaking() ? 1 : 0;
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

    public static final NoMatch NO_MATCH = new NoMatch();
    public static final FullMatch FULL_MATCH = new FullMatch(false);
    public static final FullMatch FULL_MATCH_BREAK = new FullMatch(true);

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

    public default boolean isFull() {
        return this instanceof FullMatch;
    }

    public default boolean isPostive() {
        return this instanceof PositiveMatch;
    }

    public default boolean isNegative() {
        return !isPostive();
    }

    public default boolean isPartial() {
        return this instanceof PartialMatch;
    }

    public default boolean isBreaking() {
        return false;
    }

    public static class NoMatch implements Match {

        @Override
        public String toString() {
            return descriptiveString();
        }

    }

    public static class PositiveMatch implements Match {

        public final boolean breaking;

        public PositiveMatch(boolean breaking) {
            this.breaking = breaking;
        }

        @Override
        public boolean isBreaking() {
            return breaking;
        }

        @Override
        public String stringValues() {
            return Match.super.stringValues() + ", breaking=" + breaking;
        }

        @Override
        public String toString() {
            return descriptiveString();
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

        @Override
        public String stringValues() {
            return super.stringValues() + " ,from=" + from + ", to=" + to;
        }

    }

}
