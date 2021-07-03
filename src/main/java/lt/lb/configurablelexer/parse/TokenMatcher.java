package lt.lb.configurablelexer.parse;

import java.util.function.Predicate;
import lt.lb.configurablelexer.lexer.Id;
import lt.lb.configurablelexer.parse.impl.ImportanceTokenMatcher;
import lt.lb.configurablelexer.parse.impl.NamedTokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public interface TokenMatcher extends Predicate<ConfToken>, Id {

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
    public default boolean test(ConfToken t) {
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
    public boolean matches(int position, ConfToken token);

    public default TokenMatcher named(String newName) {
        
        return new NamedTokenMatcher(newName, this);
    }

    public default TokenMatcher importance(int newImportance) {
        return new ImportanceTokenMatcher(newImportance, this);
    }

    @Override
    public default Object id() {
        return name();
    }
    
    
}
