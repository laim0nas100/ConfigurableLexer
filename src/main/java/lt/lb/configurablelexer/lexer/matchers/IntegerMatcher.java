package lt.lb.configurablelexer.lexer.matchers;

import java.util.regex.Pattern;

/**
 *
 * @author laim0nas100
 */
public class IntegerMatcher extends RegexMatcher {

    public IntegerMatcher() {
        pattern = Pattern.compile("\\d+");
    }


    @Override
    public String id() {
        return  getClass().getName() + (breaking ? ":breaking:" : "");
    }

    @Override
    public int minSize() {
        return 1;
    }
    
    

}
