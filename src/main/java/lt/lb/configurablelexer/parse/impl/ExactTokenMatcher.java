package lt.lb.configurablelexer.parse.impl;

import java.util.Objects;
import lt.lb.configurablelexer.token.ConfToken;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author laim0nas100
 */
public class ExactTokenMatcher<T extends ConfToken> extends BaseTokenMatcher<T> {

    protected boolean ignoreCase = false;
    protected String word;

    public ExactTokenMatcher(boolean ignoreCase, String name, String word) {
        super(1, name);
        this.word = Objects.requireNonNull(word, "Provided word should not be null");
        this.ignoreCase = ignoreCase;
    }

    @Override
    public boolean matches(int position, T token) {
        String tVal = token.getValue();
        return ignoreCase ? StringUtils.equalsIgnoreCase(word, tVal) : StringUtils.equals(word, tVal);
    }

}
