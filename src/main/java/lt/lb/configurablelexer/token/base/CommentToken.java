package lt.lb.configurablelexer.token.base;

/**
 *
 * @author laim0nas100
 */
public class CommentToken<T> extends StringToken<T> {

    public CommentToken() {
    }

    public CommentToken(String value) {
        super(value);
    }

    public CommentToken(String value, T info) {
        super(value, info);
    }
}
