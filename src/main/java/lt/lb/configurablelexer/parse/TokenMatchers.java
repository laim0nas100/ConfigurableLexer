package lt.lb.configurablelexer.parse;

import lt.lb.configurablelexer.parse.impl.AnyTokenMatcher;
import lt.lb.configurablelexer.parse.impl.BaseTokenMatcher;
import lt.lb.configurablelexer.parse.impl.ConcatTokenMatcher;
import lt.lb.configurablelexer.parse.impl.ConjuctionTokenMatcher;
import lt.lb.configurablelexer.parse.impl.DisjunctionTokenMatcher;
import lt.lb.configurablelexer.parse.impl.ExactTokenMatcher;
import lt.lb.configurablelexer.parse.impl.LiteralTokenMatcher;
import lt.lb.configurablelexer.parse.impl.NoneTokenMatcher;
import lt.lb.configurablelexer.parse.impl.TypeTokenMatcher;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.base.LiteralToken;

/**
 *
 * @author laim0nas100
 */
public abstract class TokenMatchers {

    public static <T extends ConfToken> TokenMatcher<T> none() {
        return new NoneTokenMatcher();
    }

    public static <T extends ConfToken> TokenMatcher<T> any(int length) {
        return new AnyTokenMatcher(length, "Any " + length);
    }

    public static <T extends ConfToken> TokenMatcher<T> any() {
        return new AnyTokenMatcher(0, "Any 0");
    }

    public static <T extends ConfToken> TokenMatcher<T> exact(String word) {
        return new ExactTokenMatcher(false, "is " + word, word);
    }

    public static <T extends ConfToken> TokenMatcher<T> exactIgnoreCase(String word) {
        return new ExactTokenMatcher(true, "ignoreCase is" + word, word);
    }

    public static <T extends ConfToken> TokenMatcher<T> ofType(Class<? extends ConfToken> type) {
        return new TypeTokenMatcher("Type:" + type.getSimpleName(), type);
    }

    public static <T extends ConfToken> TokenMatcher<T> literalType() {
        return new TypeTokenMatcher("Literal", LiteralToken.class);
    }

    public static <T extends ConfToken> TokenMatcher<T> literal(String word) {
        return new LiteralTokenMatcher(false, "Literal of " + word, word);
    }

    public static <T extends ConfToken> TokenMatcher<T> literalIgnoreCase(String word) {
        return new LiteralTokenMatcher(true, "Literal IC of " + word, word);
    }

    public static <T extends ConfToken> TokenMatcher<T> or(TokenMatcher<T>... matchers) {
        return new DisjunctionTokenMatcher("Or", matchers);
    }

    public static <T extends ConfToken> TokenMatcher<T> and(TokenMatcher<T>... matchers) {
        return new ConjuctionTokenMatcher("And", matchers);
    }

    public static <T extends ConfToken> TokenMatcher<T> concat(TokenMatcher<T>... matchers) {
        BaseTokenMatcher.assertArray(matchers);
        StringBuilder name = new StringBuilder(matchers[0].name());
        for (int i = 1; i < matchers.length; i++) {
            name.append("+").append(matchers[i].name());
        }
        return new ConcatTokenMatcher(name.toString(), matchers);
    }

}
