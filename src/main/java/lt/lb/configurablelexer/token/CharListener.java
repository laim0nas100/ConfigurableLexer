package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface CharListener {

    /**
     * You can manually check every char that is being parsed, for example count
     * newlines or global position in file
     *
     * @param isTokenChar
     * @param c
     */
    public void listen(boolean isTokenChar, int c);

    public default void reset() {

    }
}
