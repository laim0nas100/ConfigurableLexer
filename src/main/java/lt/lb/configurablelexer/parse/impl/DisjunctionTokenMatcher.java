package lt.lb.configurablelexer.parse.impl;

import java.util.Arrays;
import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class DisjunctionTokenMatcher<T extends ConfToken> extends CompositeTokenMatcher<T> {
    
    protected Class<? extends ConfToken>[] broadTypes;
    
    public DisjunctionTokenMatcher(String name, TokenMatcher... matchers) {
        super(assertSameLength(matchers), name, matchers);
        broadTypes = new Class[length];
        
        if (length > 0) {
            for (int pos = 0; pos < length; pos++) {
                Class<? extends ConfToken> requiredType = matchers[0].requiredType(pos);
                if (!ConfToken.class.isAssignableFrom(requiredType)) {
                    throw new IllegalArgumentException(matchers[0].name() + " returns type that is not a subtype of " + ConfToken.class);
                }
                broadTypes[pos] = matchers[0].requiredType(pos);
                
                for (int i = 1; i < matchers.length; i++) {
                    Class maybeBroader = matchers[i].requiredType(pos);
                    if (!ConfToken.class.isAssignableFrom(maybeBroader)) {
                        throw new IllegalArgumentException(matchers[i].name() + " returns type that is not a subtype of " + ConfToken.class);
                    }
                    
                    while (ConfToken.class.isAssignableFrom(maybeBroader)) {
                        boolean greater = typeComparator.compare(broadTypes[pos], maybeBroader) > 0;
                        if (greater) { // found common subtype
                            break;
                        } else {
                            if(maybeBroader.getSuperclass() == null){
                                break;
                            }
                            maybeBroader = maybeBroader.getSuperclass();
                        }
                    }
                    broadTypes[pos] = maybeBroader;
                }
            }
            
        }
        
    }
    
    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return broadTypes[position];
    }
    
    @Override
    public boolean matches(int position, ConfToken token) {
        for (TokenMatcher matcher : matchers) {
            if (matcher.matches(position, token)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean isRepeating() {
        for (TokenMatcher matcher : matchers) {
            if (matcher.isRepeating()) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public String stringValues() {
        return super.stringValues() + "broadTypes=" + Arrays.asList(broadTypes);
    }
    
}
