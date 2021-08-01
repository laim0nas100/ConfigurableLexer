package lt.lb.configurablelexer.token;

import java.util.Objects;

/**
 *
 * @author laim0nas100
 * @param <T>
 */
public class SettableConfTokenizer<T extends ConfToken> implements DelegatingConfTokenizer {

    protected ConfTokenizer<T> tokenizer;
    
    @Override
    public ConfTokenizer delegate() {
        return Objects.requireNonNull(tokenizer, "SettableConfTokenizer is not set");
    }

    public ConfTokenizer<T> getTokenizer() {
        return tokenizer;
    }

    public void setTokenizer(ConfTokenizer<T> tokenizer) {
        this.tokenizer = tokenizer;
    }
    
}
