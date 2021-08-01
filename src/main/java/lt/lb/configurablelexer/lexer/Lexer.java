package lt.lb.configurablelexer.lexer;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 * @param <T>
 */
public interface Lexer<T extends ConfToken> extends TokenizerCallbacks<T> {

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception;

}
