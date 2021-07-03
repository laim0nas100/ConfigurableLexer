package lt.lb.configurablelexer.parse.impl;

import java.util.Objects;
import lt.lb.configurablelexer.parse.TokenMatcher;

/**
 *
 * @author laim0nas100
 */
public class NamedTokenMatcher extends DelegatedTokenMatcher {

    protected String name;

    public NamedTokenMatcher(String name, TokenMatcher main) {
        super(main);
        this.name = Objects.requireNonNull(name, "Name should not be null");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return name + " " + super.toString();
    }

}
