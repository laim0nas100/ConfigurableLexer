package lt.lb.configurablelexer.lexer.matchers;

import java.util.regex.Pattern;

/**
 *
 * @author laim0nas100
 */
public class FloatMatcher extends RegexMatcher {

    public FloatMatcher(char sep, boolean optionalLeadingNumber) {
        if (optionalLeadingNumber) {
            pattern = Pattern.compile("\\d*\\" + sep + "\\d+");
        } else {
            pattern = Pattern.compile("\\d+\\" + sep + "\\d+");
        }

    }

    public FloatMatcher() {
        this('.', false);
    }

}
