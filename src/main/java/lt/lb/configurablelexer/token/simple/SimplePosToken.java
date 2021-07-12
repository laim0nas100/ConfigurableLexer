package lt.lb.configurablelexer.token.simple;

/**
 *
 * @author laim0nas100
 */
public class SimplePosToken extends SimpleToken<Pos> {

    public SimplePosToken(Pos pos, String val) {
        this.info = pos;
        this.value = val;
    }

    public SimplePosToken() {
    }

}
