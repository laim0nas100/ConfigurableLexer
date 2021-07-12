package lt.lb.configurablelexer.token.spec;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.DelegatingTokenizerCallbacks;

/**
 *
 * @author laim0nas100
 */
public interface ExtendedPositionAwareSplittableCallback<T extends ConfToken, PosInfo> extends DelegatingTokenizerCallbacks<T> {

    public void resetInternalState();

    public PosInfo start();

    public PosInfo end();

    public PosInfo mid();

    public boolean isWithin();

    public boolean isIgnore();

    public boolean isEarlyReturn();

    public void setIgnore(boolean bool);

    public void setEarlyReturn(boolean bool);

    /**
     *
     * @param buffer
     * @param offset
     * @param length
     * @return
     * @throws Exception
     */
    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception;

    public T construct(PosInfo start, PosInfo end, char[] buffer, int offset, int length) throws Exception;

}
