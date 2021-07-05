package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface TokenizerCallbacks<T extends ConfToken> extends ConfTokenConstructor<T> {

    /**
     * Reset anything that is resettable, like {@link ConfTokenizer} and {@link CharListener}
     */
    public default void reset() {

    }

    /**
     * Tokenizer can keep track of chars that has been read.
     *
     * @param isTokenChar
     * @param isBreakChar
     * @param c
     */
    public void charListener(boolean isTokenChar, boolean isBreakChar, int c);

    /**
     * If char can be a part of a token. Should split char stream if non-token
     * char is found;
     *
     * @param c
     * @return
     */
    public boolean isTokenChar(int c);

    /**
     * Extension of {@link TokenizerCallbacks#isTokenChar(int) }. Tokenizer can
     * break on included chars also, but it is rare.
     *
     * @param isTokenChar
     * @param c
     * @return
     */
    public default boolean isBreakChar(boolean isTokenChar, int c) {
        return !isTokenChar;
    }

}
