package lt.lb.configurablelexer.token;

import lt.lb.configurablelexer.DisableAware;

/**
 *
 * @author laim0nas100
 */
public interface CharListener extends DisableAware.DisableAwareDefault {

    /**
     * You can manually check every char that is being parsed, for example count
     * newlines or global position in file
     *
     * @param chInfo
     * @param c
     */
    public void charListener(CharInfo chInfo, int c);

    public default void reset() {

    }
}
