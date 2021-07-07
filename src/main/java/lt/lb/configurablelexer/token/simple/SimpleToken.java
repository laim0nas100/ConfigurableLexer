package lt.lb.configurablelexer.token.simple;

import lt.lb.configurablelexer.token.base.BaseStringToken;

/**
 *
 * @author laim0nas100
 */
public class SimpleToken<T> extends BaseStringToken<T> {

    public SimpleToken() {
    }

    public SimpleToken(String val) {
        this.value = val;
    }

}
