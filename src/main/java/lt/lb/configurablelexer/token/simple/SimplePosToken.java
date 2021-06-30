package lt.lb.configurablelexer.token.simple;

/**
 *
 * @author laim0nas100
 */
public class SimplePosToken extends SimpleToken<Pos> {

    protected Pos pos;

    public SimplePosToken(Pos pos, String val) {
        super(val);
        this.pos = pos;
    }

    @Override
    public boolean infoAvailable() {
        return pos != null;
    }

    @Override
    public Pos getInfo() {
        return pos;
    }

    @Override
    public String toString() {
        return "SimplePosToken{" + "pos=" + pos + ", value="+value+"}";
    }
    
    

}
