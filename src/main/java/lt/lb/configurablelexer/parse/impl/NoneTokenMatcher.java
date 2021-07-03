package lt.lb.configurablelexer.parse.impl;

import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public class NoneTokenMatcher extends BaseTokenMatcher {

    /**
     * Should never match with this class
     */
    public static final class ConfTokenNone implements ConfToken {

        private ConfTokenNone() {
        }

        
        @Override
        public String getValue() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }

    public NoneTokenMatcher() {
        this(0, "None");
    }

    public NoneTokenMatcher(int length, String name) {
        super(length, name);
    }

    @Override
    public boolean matches(int position, ConfToken token) {
        return false;
    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return ConfTokenNone.class;
    }

}
