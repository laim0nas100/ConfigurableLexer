package lt.lb.configurablelexer.token.simple;

import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class SimpleToken<T> implements ConfToken<T> {

    protected String value;

    public SimpleToken(String val) {
        this.value = val;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "SimpleToken{" + "value=" + value + '}';
    }
    
    

}
