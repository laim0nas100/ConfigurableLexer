package lt.lb.configurablelexer.token;

/**
 *
 * @author laim0nas100
 */
public interface CharInfo {

    public boolean isTokenChar();

    public boolean isBreakChar();

    public boolean isLastChar();

    public static class CharInfoDefault implements CharInfo {

        protected final boolean tokenChar;
        protected final boolean breakChar;
        protected final boolean lastChar;

        public CharInfoDefault(boolean tokenChar, boolean breakChar, boolean lastChar) {
            this.tokenChar = tokenChar;
            this.breakChar = breakChar;
            this.lastChar = lastChar;
        }
        
        public static CharInfoDefault of(boolean isToken, boolean isBreak, boolean isLast){
            if(isToken){
                if(isBreak){
                    return isLast ? TOKEN_BREAK_LAST : TOKEN_BREAK_NOLAST;
                }else{
                    return isLast ? TOKEN_NOBREAK_LAST : TOKEN_NOBREAK_NOLAST;
                }
            }else{
                if(isBreak){
                    return isLast ? NOTOKEN_BREAK_LAST : NOTOKEN_BREAK_NOLAST;
                }else{
                    return isLast ? NOTOKEN_NOBREAK_LAST : NOTOKEN_NOBREAK_NOLAST;
                }
            }
        }

        @Override
        public String toString() {
            return "CharInfoDefault{" + (tokenChar ? "tokenChar " :"")+ (breakChar ? "breakChar ": "") + (lastChar ? "lastChar ":"")+"}";
        }
        
        

        @Override
        public boolean isTokenChar() {
            return tokenChar;
        }

        @Override
        public boolean isBreakChar() {
            return breakChar;
        }

        @Override
        public boolean isLastChar() {
            return lastChar;
        }

        public static final CharInfoDefault NOTOKEN_NOBREAK_NOLAST = new CharInfoDefault(false, false, false);
        public static final CharInfoDefault NOTOKEN_NOBREAK_LAST = new CharInfoDefault(false, false, true);
        public static final CharInfoDefault NOTOKEN_BREAK_NOLAST = new CharInfoDefault(false, true, false);
        public static final CharInfoDefault NOTOKEN_BREAK_LAST = new CharInfoDefault(false, true, true);
        public static final CharInfoDefault TOKEN_NOBREAK_NOLAST = new CharInfoDefault(true, false, false);
        public static final CharInfoDefault TOKEN_NOBREAK_LAST = new CharInfoDefault(true, false, true);
        public static final CharInfoDefault TOKEN_BREAK_NOLAST = new CharInfoDefault(true, true, false);
        public static final CharInfoDefault TOKEN_BREAK_LAST = new CharInfoDefault(true, true, true);

    }
}
