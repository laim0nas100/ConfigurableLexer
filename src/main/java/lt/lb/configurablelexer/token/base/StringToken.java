package lt.lb.configurablelexer.token.base;

/**
 *
 * @author laim0nas100
 */
public class StringToken<Inf> extends BaseStringToken<Inf> {

    public StringToken() {
    }

    public StringToken(String value) {
        super(value);
    }

    public StringToken(String value, Inf info) {
        super(value, info);
    }

}
