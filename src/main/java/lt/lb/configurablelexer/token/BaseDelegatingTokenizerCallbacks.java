package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public class BaseDelegatingTokenizerCallbacks<T extends ConfToken> implements DelegatingTokenizerCallbacks<T> {

    protected TokenizerCallbacks<T> delegate;

    public BaseDelegatingTokenizerCallbacks(TokenizerCallbacks<T> delegate) {
        this.delegate = delegate;
    }

    public BaseDelegatingTokenizerCallbacks() {
    }

    public void setDelegate(TokenizerCallbacks<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public TokenizerCallbacks<T> delegate() {
        return delegate;
    }

}
