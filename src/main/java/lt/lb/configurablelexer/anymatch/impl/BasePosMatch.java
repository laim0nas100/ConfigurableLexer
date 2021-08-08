package lt.lb.configurablelexer.anymatch.impl;

import lt.lb.configurablelexer.anymatch.PosMatch;

/**
 *
 * @author laim0nas100
 * @param <T>
 * @param <M>
 */
public abstract class BasePosMatch<T, M> implements PosMatch<T,M> {
    
    protected boolean repeating = false;
    protected int importance = 0;
    protected int length = 1;
    protected M name;
    
    
    @Override
    public int getLength() {
        return length;
    }

    @Override
    public boolean isRepeating() {
        return repeating;
    }

    @Override
    public int getImportance() {
        return importance;
    }

    @Override
    public String stringValues() {
        return PosMatch.super.stringValues() + " repeating=" + repeating + ", importance=" + importance + ", length=" + length;
    }

    @Override
    public M getName() {
        return name;
    }

    public void setName(M name) {
        this.name = name;
    }
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public void setLength(int length) {
        this.length = length;
    }
    
    
}
