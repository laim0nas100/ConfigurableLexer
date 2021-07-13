package lt.lb.configurablelexer.parse;

import java.util.Arrays;
import java.util.function.Predicate;
import lt.lb.configurablelexer.Id;
import lt.lb.configurablelexer.parse.impl.ConjuctionTokenMatcher;
import lt.lb.configurablelexer.parse.impl.DisjunctionTokenMatcher;
import lt.lb.configurablelexer.parse.impl.ImportanceTokenMatcher;
import lt.lb.configurablelexer.parse.impl.NamedTokenMatcher;
import lt.lb.configurablelexer.parse.impl.RepeatableTokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public interface TokenMatcher<T extends ConfToken> extends Predicate<T>, Id {

    /**
     * How to recognize what has matched
     *
     * @return
     */
    public String name();

    /**
     * How many tokens are required. 0 or below means it is never used.
     *
     * @return
     */
    public default int length() {
        return 1;
    }

    /**
     * If the sequence can be repeating.
     *
     * @return
     */
    public default boolean isRepeading() {
        return false;
    }

    /**
     * Higher importance means it is tried applied sooner
     *
     * @return
     */
    public default int importance() {
        return 0;
    }

    /**
     * Supply required most narrow subtype for optimized matching
     *
     * @param position should be within length
     * @return
     */
    public default Class<? extends ConfToken> requiredType(int position) {
        return ConfToken.class;
    }

    @Override
    public default boolean test(T t) {
        for (int i = 0; i < this.length(); i++) {
            if (matches(i, t)) {
                return true;
            }
        }
        return false;
    }

    /**
     * If given token can be at given position
     *
     * @param position should be within length (negative)
     * @param token
     * @return
     */
    public boolean matches(int position, T token);

    public default TokenMatcher named(String newName) {
        return new NamedTokenMatcher(newName, this);
    }

    public default TokenMatcher orWith(TokenMatcher or) {
        return new DisjunctionTokenMatcher("Or", this, or);
    }
    
    public default TokenMatcher andWith(TokenMatcher and) {
        return new ConjuctionTokenMatcher("And", this, and);
    }

    public default TokenMatcher importance(int newImportance) {
        return new ImportanceTokenMatcher(newImportance, this);
    }

    public default TokenMatcher repeating(boolean rep) {
        return new RepeatableTokenMatcher(rep, this);
    }

    @Override
    public default Object id() {
        return name();
    }

}
