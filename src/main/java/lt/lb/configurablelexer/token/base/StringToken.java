package lt.lb.configurablelexer.token.base;

import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class StringToken<Inf> implements ConfToken<Inf> {

    protected String value;
    protected Inf info;

    public StringToken() {
    }

    public StringToken(String value) {
        this.value = value;
    }
    
    
    public StringToken(String value, Inf info) {
        this.value = value;
        this.info = info;
    }
    
    
    
    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public Inf getInfo() {
        return info;
    }

    public void setInfo(Inf info) {
        this.info = info;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()+"{" + "value=" + value + ", info=" + info + '}';
    }
    
    
    
}
