package lt.lb.configurablelexer.token.base;

/**
 *
 * @author laim0nas100
 */
public class NumberToken<Inf> extends ProcessedToken<Inf, Number> {

    public NumberToken() {
    }

    public NumberToken(String value) {
        super(value);
    }

    public NumberToken(String value, Inf info) {
        super(value, info);
    }

}
