package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public class DefaultConfTokenizer<T extends ConfToken> extends BaseTokenizer<T> {

    protected ConfTokenizerCallbacks<T> callbacks;

    public DefaultConfTokenizer() {
        callbacks = new ConfTokenizerCallbacks<>();
        callbacks.setNestedWithin(this);
    }

    @Override
    public ConfTokenizerCallbacks<T> getCallbacks() {
        return callbacks;
    }

    public void setCallbacks(ConfTokenizerCallbacks<T> callbacks) {
        this.callbacks = callbacks;
    }

}
