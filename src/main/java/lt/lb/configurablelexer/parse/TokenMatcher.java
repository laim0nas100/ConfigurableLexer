package lt.lb.configurablelexer.parse;

import lt.lb.configurablelexer.Id;
import lt.lb.configurablelexer.anymatch.PosMatch;
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
@Deprecated
public interface TokenMatcher<T extends ConfToken> extends PosMatch<T, String>, Id {

    /**
     * How to recognize what has matched
     *
     * @return
     */
    public String name();

    @Override
    public default String getName() {
        return name();
    }

    /**
     * How many tokens are required. 0 or below means it is never used.
     *
     * @return
     */
    @Override
    public default int getLength() {
        return 1;
    }

    /**
     * If the sequence can be repeating.
     *
     * @return
     */
    @Override
    public default boolean isRepeating() {
        return false;
    }

    /**
     * Higher importance means it is tried applied sooner
     *
     * @return
     */
    @Override
    public default int getImportance() {
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
