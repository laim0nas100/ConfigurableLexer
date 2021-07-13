package test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Pattern;
import lt.lb.configurablelexer.Redirecter;
import lt.lb.configurablelexer.lexer.SimpleLexer;
import lt.lb.configurablelexer.lexer.matchers.FloatMatcher;
import lt.lb.configurablelexer.lexer.matchers.IntegerMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.lexer.matchers.KeywordMatcher;
import lt.lb.configurablelexer.lexer.matchers.RegexMatcher;
import lt.lb.configurablelexer.parse.DefaultMatchedTokenProducer;
import lt.lb.configurablelexer.parse.TokenMatcher;
import lt.lb.configurablelexer.parse.TokenMatchers;
import lt.lb.configurablelexer.token.base.KeywordToken;
import lt.lb.configurablelexer.token.base.BaseStringToken;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.DefaultConfTokenizer;
import lt.lb.configurablelexer.token.spec.LineAwareCharListener;
import lt.lb.configurablelexer.token.base.CommentToken;
import lt.lb.configurablelexer.token.base.LiteralToken;
import lt.lb.configurablelexer.token.base.NumberToken;
import lt.lb.configurablelexer.token.base.StringToken;
import lt.lb.configurablelexer.token.simple.Pos;
import lt.lb.configurablelexer.token.spec.ExtendedPositionAwareSplittableCallback;
import lt.lb.configurablelexer.token.spec.comment.CommentAwareCallback;
import lt.lb.configurablelexer.token.spec.comment.PosAwareDefaultCallback;
import lt.lb.configurablelexer.token.spec.string.StringAwareCallback;

/**
 *
 * @author laim0nas100
 */
public class MAINParse01 {

    public static class IdentifierMatcher extends RegexMatcher {

        public IdentifierMatcher() {
            pattern = Pattern.compile("[A-Za-z][A-Za-z0-9_]*");
        }

    }

    public static class OperatorMatcher extends KeywordMatcher {

        public OperatorMatcher() {
        }

        public OperatorMatcher(String val, boolean ignoreCase, boolean breaking) {
            super(val, ignoreCase, breaking);
        }

        public OperatorMatcher(String val, boolean breaking) {
            super(val, breaking);
        }

        public OperatorMatcher(String val) {
            super(val);
        }

    }

    public static class IdentifierToken<Inf> extends BaseStringToken<Inf> {

        public IdentifierToken() {
        }

        public IdentifierToken(String value) {
            super(value);
        }

        public IdentifierToken(String value, Inf info) {
            super(value, info);
        }

    }

    public static class OperatorToken<Inf> extends BaseStringToken<Inf> {

        public OperatorToken() {
        }

        public OperatorToken(String value) {
            super(value);
        }

        public OperatorToken(String value, Inf info) {
            super(value, info);
        }

    }

    public static void main(String[] args) throws Exception {

        URL resource = Redirecter.class.getResource("/parse_text_expression.txt");
        Reader input = new FileReader(resource.getFile(), StandardCharsets.UTF_8);

        DefaultConfTokenizer<ConfToken> tokenizer = new DefaultConfTokenizer();

        LineAwareCharListener lineListener = new LineAwareCharListener();

        tokenizer.getConfCallbacks()
                .setTokenCharPredicate(
                        new ConfCharPredicate().disallowWhen(Character::isWhitespace)
                )
                .addListener(lineListener);

        SimpleLexer lexer = tokenizer.nest(t -> new SimpleLexer<ConfToken>(t) {
            @Override
            public BaseStringToken<Pos> makeLexeme(int from, int to, StringMatcher.MatcherMatch matcher, String str) throws Exception {
                Pos pos = lineListener.getPos(from, str.length());
                String val = str.substring(from, to);
                if (matcher.matcher instanceof KeywordMatcher) {
                    if (matcher.matcher instanceof OperatorMatcher) {
                        return new OperatorToken<>(val, pos);
                    } else {
                        return new KeywordToken<>(val, pos);
                    }
                }

                if (matcher.matcher instanceof IntegerMatcher) {
                    NumberToken<Pos> numberToken = new NumberToken<>(val, pos);
                    numberToken.processValue(Integer::parseInt);
                    return numberToken;
                }

                if (matcher.matcher instanceof FloatMatcher) {
                    NumberToken<Pos> numberToken = new NumberToken<>(val, pos);
                    numberToken.processValue(Float::parseFloat);
                    return numberToken;
                }

                if (matcher.matcher instanceof IdentifierMatcher) {
                    return new IdentifierToken<>(val, pos);
                }
                throw new IllegalArgumentException("Unrecognized matcher");
            }

            @Override
            public BaseStringToken<Pos> makeLiteral(int from, int to, String str) throws Exception {
                Pos pos = lineListener.getPos(from, str.length());
                return new LiteralToken<>(str.substring(from, to), pos);
            }
        });
        tokenizer.getConfCallbacks().nest(t -> {
            return new PosAwareDefaultCallback<ConfToken, Pos>(t, lineListener::getPos) {
                @Override
                public ConfToken construct(ExtendedPositionAwareSplittableCallback cb, Pos start, Pos end, char[] buffer, int offset, int length) throws Exception {
                    if (cb instanceof CommentAwareCallback) {
                        return new CommentToken(String.valueOf(buffer, offset, length), start);
                    }
                    if (cb instanceof StringAwareCallback) {
                        return new StringToken(String.valueOf(buffer, offset, length), start);
                    }
                    throw new IllegalStateException("Unrecognized callback " + cb);
                }

            }
                    .enableLineComment('#', '$')
                    .enableLineComment("//")
                    .enableMultilineComment("/*", "*/", false, true)
                    .enableStrings()
                    .enableExclusion(true)
                    .ignoringOnlyComments(true);
        });

        lexer.addMatcher(new IntegerMatcher());
        lexer.addMatcher(new FloatMatcher());
        lexer.addMatcher(new IdentifierMatcher());
        lexer.addMatcher(new OperatorMatcher("+", true));
        lexer.addMatcher(new OperatorMatcher("-", true));
        lexer.addMatcher(new OperatorMatcher("*", true));
        lexer.addMatcher(new OperatorMatcher("/", true));
        lexer.addMatcher(new KeywordMatcher("=", true));
        lexer.addMatcher(new KeywordMatcher("++", true));
        lexer.addMatcher(new KeywordMatcher(";", true));
        lexer.addMatcher(new KeywordMatcher("int", false));
        lexer.addMatcher(new KeywordMatcher("float", false));
        lexer.addMatcher(new KeywordMatcher("[", true));
        lexer.addMatcher(new KeywordMatcher("]", true));
        lexer.addMatcher(new KeywordMatcher(",", true));

        ConfTokenizer myTokenizer = tokenizer;
        myTokenizer.reset(input);

        TokenMatcher any = TokenMatchers.any(1).importance(-1); // will appear at the end of matched list. Default importance is 0
        TokenMatcher number = TokenMatchers.ofType(NumberToken.class);
        TokenMatcher op = TokenMatchers.ofType(OperatorToken.class);
        TokenMatcher type = TokenMatchers.or(TokenMatchers.exact("int"), TokenMatchers.exact("float"));
        TokenMatcher identifier = TokenMatchers.ofType(IdentifierToken.class);
        TokenMatcher eq = TokenMatchers.exact("=");
        TokenMatcher variable = number.orWith(identifier).named("var");

        TokenMatcher exprStart = TokenMatchers.concat(variable, op).named("exp start");
        TokenMatcher expMid = TokenMatchers.concat(variable, op, number).named("exp");
        TokenMatcher expEnd = TokenMatchers.concat(op, variable).named("exp end");
        TokenMatcher end = TokenMatchers.concat(op, variable, TokenMatchers.exact(";")).named("end");

        TokenMatcher arrayStart = TokenMatchers.concat(TokenMatchers.exact("[")).named("array start");
        TokenMatcher arrayStart1 = TokenMatchers.concat(TokenMatchers.exact("["), variable).named("array start 1");
        TokenMatcher arrayCont = TokenMatchers.concat(TokenMatchers.exact(","), variable).named("arrayCont").repeating(true);

        TokenMatcher arrayEnd = TokenMatchers.concat(TokenMatchers.exact("]")).named("array end");

        TokenMatcher assignment = TokenMatchers.concat(type, identifier, eq).named("Assignment");

        TokenMatcher identiefierSequence = identifier.repeating(true).named("Identifier seq");

        myTokenizer.reset(input);

        DefaultMatchedTokenProducer defaultMatchedTokenProducer = new DefaultMatchedTokenProducer<>(myTokenizer, 
                Arrays.asList(identiefierSequence, assignment, exprStart, expMid, expEnd, end, any, arrayStart, arrayStart1, arrayCont, arrayEnd)
        );

        StringBuilder sb = new StringBuilder();

        defaultMatchedTokenProducer.toSimplifiedIterator().forEach(m -> {
            sb.append(m).append("\n");
        });
        System.out.println(sb);

        input.close();

    }
}
