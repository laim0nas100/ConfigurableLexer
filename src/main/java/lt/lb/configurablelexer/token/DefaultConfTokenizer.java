package lt.lb.configurablelexer.token;

import java.util.function.Function;
import lt.lb.configurablelexer.Redirecter;

/**
 *
 * @author laim0nas100
 */
public class DefaultConfTokenizer<T extends ConfToken> extends BaseTokenizer<T> {

    protected ConfTokenizerCallbacks<T> callbacks;
    protected ConfTokenizer<T> nestedWithin;

    public DefaultConfTokenizer() {
        callbacks = new ConfTokenizerCallbacks<>();
        callbacks.setNestedWithin(this);
        nestedWithin = this;
    }

    public ConfTokenizerCallbacks<T> getConfCallbacks() {
        return callbacks;
    }

    @Override
    protected TokenizerCallbacks<T> getCallbacks() {
        return getConfCallbacks();
    }
    
    

    public void setCallbacks(ConfTokenizerCallbacks<T> callbacks) {
        this.callbacks = callbacks;
    }

    public <N extends ConfTokenizer<T>> N nest(Function<ConfTokenizer<T>, N> function) {
        N apply = function.apply(nestedWithin);
        nestedWithin = apply;
        getConfCallbacks().nest(f->apply);
        return apply;
    }

}
