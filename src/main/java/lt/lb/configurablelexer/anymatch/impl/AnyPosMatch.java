package lt.lb.configurablelexer.anymatch.impl;

/**
 *
 * @author laim0nas100
 */
public class AnyPosMatch<T,M> extends BasePosMatch<T,M>{

    public AnyPosMatch(int length) {
        this.length = length;
    }

    public AnyPosMatch() {
        this(1);
    }
    
    @Override
    public boolean matches(int position, T item) {
        return true;
    }
    
}
