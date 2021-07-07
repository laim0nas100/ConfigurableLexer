package lt.lb.configurablelexer.token.base;

/**
 *
 * @author laim0nas100
 */
public class LiteralToken<Inf> extends BaseStringToken<Inf> {

    public LiteralToken() {
    }

    public LiteralToken(String value) {
        super(value);
    }

    public LiteralToken(String value, Inf info) {
        super(value, info);
    }
    
}
