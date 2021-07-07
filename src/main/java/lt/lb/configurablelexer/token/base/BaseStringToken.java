package lt.lb.configurablelexer.token.base;

import lt.lb.configurablelexer.Id;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 * @param <Inf>
 */
public class BaseStringToken<Inf> implements ConfToken<Inf>, Id {

    protected String value;
    protected Inf info;

    public BaseStringToken() {
    }

    public BaseStringToken(String value) {
        this.value = value;
    }

    public BaseStringToken(String value, Inf info) {
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
        return descriptiveString();
    }

    @Override
    public String stringValues() {
        return "value=" + value + " ,info=" + info;
    }

}
