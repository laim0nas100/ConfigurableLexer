package lt.lb.configurablelexer.anymatch.impl;

import lt.lb.configurablelexer.anymatch.PosMatched;

/**
 *
 * @author laim0nas100
 */
public class NameLiftPosMatch<T, M> extends BaseLiftPosMatch<T, M> {

    protected M name;

    public NameLiftPosMatch(M name) {
        this.name = name;
        this.length = 1;
    }

    @Override
    public boolean matches(int position, PosMatched<T, M> item) {
        return item.containsMatcher(name);
    }


}
