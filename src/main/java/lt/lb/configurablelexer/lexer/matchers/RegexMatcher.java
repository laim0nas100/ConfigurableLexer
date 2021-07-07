package lt.lb.configurablelexer.lexer.matchers;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lt.lb.commons.DLog;
import lt.lb.configurablelexer.Id;

/**
 *
 * @author laim0nas100
 */
public class RegexMatcher extends BreakingSerStringMatcher {

    protected Pattern pattern;

    @Override
    public Match match(String str, int offset, int localLength) {
        Objects.requireNonNull(pattern, "Pattern was not set");
        Matcher matcher = pattern.matcher(str);

        boolean find = matcher.find(offset);

        if (!find) {
            return Match.noMatch();
        }

        int start = matcher.start();
        int end = matcher.end();
        int len = end - start;
        if (start == offset && len == localLength) {
            return makeMatch();
        } else if (len > localLength) {
            return Match.noMatch();
        } else {
            return makeMatch(start, end);
        }
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    @Override
    public String stringValues() {
        return super.stringValues()+ ", pattern="+pattern.pattern();
    }
    
    

}
