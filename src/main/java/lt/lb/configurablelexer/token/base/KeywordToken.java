package lt.lb.configurablelexer.token.base;

/**
 *
 * @author laim0nas100
 */
public class KeywordToken<Inf> extends BaseStringToken<Inf> {

    public KeywordToken() {
    }

    public KeywordToken(String value) {
        super(value);
    }

    public KeywordToken(String value, Inf info) {
        super(value, info);
    }

}
