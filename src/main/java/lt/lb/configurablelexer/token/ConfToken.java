package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface ConfToken<Inf> {

    public default boolean infoAvailable() {
        return false;
    }

    public default Inf getInfo() {
        throw new UnsupportedOperationException("Unimplemented getInfo");
    }

    public String getValue();
}
