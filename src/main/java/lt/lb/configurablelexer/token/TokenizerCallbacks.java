package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface TokenizerCallbacks<T extends ConfToken> extends ConfTokenConstructor<T>, CharListener {

    /**
     * Reset anything that is resettable, like {@link ConfTokenizer} and
     * {@link CharListener}
     */
    public default void reset() {

    }

    /**
     * If char can be a part of a token. Should split char stream if non-token
     * codepoint is found.
     *
     * It is not always called, use {@link TokenizerCallbacks#charListener(boolean, boolean, int)
     * } instead to visit every codepoint.
     *
     * @param c codepoint
     * @return
     */
    public boolean isTokenChar(int c);

    /**
     * Extension of {@link TokenizerCallbacks#isTokenChar(int) }. Tokenizer
     * should be able break on included chars also, but it is rare.
     *
     * It is not always called, use {@link TokenizerCallbacks#charListener(boolean, boolean, int)
     * } instead to visit every codepoint.
     *
     * @param c codepoint
     * @return
     */
    public boolean isBreakChar(int c);
}
