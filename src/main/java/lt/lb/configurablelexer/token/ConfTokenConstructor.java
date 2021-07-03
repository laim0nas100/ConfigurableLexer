package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface ConfTokenConstructor<T extends ConfToken> {

    public static final ConfTokenConstructor NOT_SET = new ConfTokenConstructor() {
        @Override
        public ConfTokenBuffer constructTokens(char[] buffer, int offset, int length) throws Exception {
            throw new IllegalStateException("Token constructor is not set");
        }
    };

    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception;
}
