package lt.lb.configurablelexer.lexer.matchers;

import java.util.Objects;

/**
 *
 * @author laim0nas100
 */
public abstract class IdStringMatcher implements StringMatcher {

    @Override
    public abstract Match match(String str, int offset, int length);

    @Override
    public abstract String id();

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof StringMatcher) {
            StringMatcher matcher = (StringMatcher) obj;
            return Objects.equals(this.id(), matcher.id());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id());
    }

}
