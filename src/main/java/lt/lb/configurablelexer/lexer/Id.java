package lt.lb.configurablelexer.lexer;

/**
 *
 * @author laim0nas100
 */
public interface Id {
     public default Object id() {
        return System.identityHashCode(this);
    }
}
