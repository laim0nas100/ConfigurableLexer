package lt.lb.configurablelexer.token.simple;

import lt.lb.configurablelexer.token.base.StringToken;

/**
 *
 * @author laim0nas100
 */
public class SimpleToken<T> extends StringToken<T> {

    public SimpleToken() {
    }

    public SimpleToken(String val) {
        this.value = val;
    }

}
