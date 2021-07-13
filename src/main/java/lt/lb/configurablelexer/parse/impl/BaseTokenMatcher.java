package lt.lb.configurablelexer.parse.impl;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;
import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;

/**
 *
 * @author laim0nas100
 */
public abstract class BaseTokenMatcher<T extends ConfToken> implements TokenMatcher<T> {

    protected int length = 1;
    protected String name = "";

    public BaseTokenMatcher(int length, String name) {
        this.length = length;
        this.name = Objects.requireNonNull(name, "Name should not be null");
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public int length() {
        return length;
    }

    @Override
    public Class<? extends ConfToken> requiredType(int position) {
        return ConfToken.class;
    }

    @Override
    public String stringValues() {
        return "name=" + name() + ", length=" + length();
    }

    @Override
    public String toString() {
        return descriptiveString();
    }

    public static <T> T[] assertArray(T... array) {
        if (array.length == 0) {
            throw new IllegalArgumentException("Empty arrays not allowed");
        }
        if (Stream.of(array).anyMatch(f -> f == null)) {
            throw new IllegalArgumentException(Arrays.asList(array) + " contains a null");
        }
        return array;
    }

}
