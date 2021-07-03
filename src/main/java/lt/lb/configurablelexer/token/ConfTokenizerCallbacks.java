package lt.lb.configurablelexer.token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;

/**
 *
 * @author laim0nas100
 */
public class ConfTokenizerCallbacks<T extends ConfToken> implements TokenizerCallbacksListeners<T> {

    protected List<CharListener> charListeners = new ArrayList<>();
    protected Set<Integer> allowedChars = new HashSet<>();
    protected Set<Integer> disallowedChars = new HashSet<>();
    protected List<IntPredicate> allowed = new ArrayList<>();
    protected List<IntPredicate> disallowed = new ArrayList<>();
    protected ConfTokenConstructor<T> constructor = ConfTokenConstructor.NOT_SET;
    
    public void allowChars(Collection<Integer> ac){
        allowedChars.addAll(ac);
    }
    
    public void allowChars(int ...chars){
        for(int c:chars){
            allowedChars.add(c);
        }
    }
    
    public void disallowChars(Collection<Integer> ac){
        disallowedChars.addAll(ac);
    }
    
    public void disallowChars(int ...chars){
        for(int c:chars){
            disallowedChars.add(c);
        }
    }
    
    public void allowWhen(IntPredicate pred){
        Objects.requireNonNull(pred, "Predicate is null");
        allowed.add(pred);
    }
    
    public void disallowWhen(IntPredicate pred){
        Objects.requireNonNull(pred, "Predicate is null");
        disallowed.add(pred);
    }
    

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return constructor.constructTokens(buffer, offset, length);
    }

    @Override
    public boolean isTokenChar(int c) {
        for(IntPredicate pred:allowed){
            if(pred.test(c)){
                return true;
            }
        }
        for(IntPredicate pred:disallowed){
            if(pred.test(c)){
                return false;
            }
        }
        if(allowedChars.contains(c)){
            return true;
        }
        return !disallowedChars.contains(c);
    }
    
    public void addListener(CharListener listener){
        charListeners.add(listener);
    }

    @Override
    public Iterable<CharListener> listeners() {
        return charListeners;
    }

    public List<CharListener> getCharListeners() {
        return charListeners;
    }

    public void setCharListeners(List<CharListener> charListeners) {
        this.charListeners = charListeners;
    }

    public Set<Integer> getAllowedChars() {
        return allowedChars;
    }

    public void setAllowedChars(Set<Integer> allowedChars) {
        this.allowedChars = allowedChars;
    }

    public Set<Integer> getDisallowedChars() {
        return disallowedChars;
    }

    public void setDisallowedChars(Set<Integer> disallowedChars) {
        this.disallowedChars = disallowedChars;
    }

    public List<IntPredicate> getAllowed() {
        return allowed;
    }

    public void setAllowed(List<IntPredicate> allowed) {
        this.allowed = allowed;
    }

    public List<IntPredicate> getDisallowed() {
        return disallowed;
    }

    public void setDisallowed(List<IntPredicate> disallowed) {
        this.disallowed = disallowed;
    }

    public ConfTokenConstructor<T> getConstructor() {
        return constructor;
    }

    public void setConstructor(ConfTokenConstructor<T> constructor) {
        this.constructor = constructor;
    }
    
    


}
