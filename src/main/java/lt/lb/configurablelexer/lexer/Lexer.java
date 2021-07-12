package lt.lb.configurablelexer.lexer;

import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.DelegatingConfTokenizer;

/**
 *
 * @author laim0nas100
 */
public interface Lexer<T extends ConfToken> extends DelegatingConfTokenizer<T> {

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception;

}
