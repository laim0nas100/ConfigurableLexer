package lt.lb.configurablelexer.token.base;

import java.util.function.Function;

/**
 *
 * @author laim0nas100
 */
public class ProcessedToken<Inf,T> extends StringToken<Inf> {
    
    protected T processedValue;

    public ProcessedToken() {
    }

    public ProcessedToken(String value) {
        super(value);
    }

    public ProcessedToken(String value, Inf info) {
        super(value, info);
    }

    public T getProcessedValue() {
        return processedValue;
    }

    public void setProcessedValue(T processedValue) {
        this.processedValue = processedValue;
    }
    
    public void processValue(Function<String,T> func){
        setProcessedValue(func.apply(getValue()));
    }

    @Override
    public String toStringValues() {
        return super.toStringValues() + ", processedValue=" + processedValue;
    }
}
