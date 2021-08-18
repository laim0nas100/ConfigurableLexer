package test;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import lt.lb.configurablelexer.Redirecter;
import lt.lb.configurablelexer.anymatch.PosMatched;
import lt.lb.configurablelexer.anymatch.impl.ConfMatchers;
import lt.lb.configurablelexer.anymatch.impl.ConfMatchers.PM;
import lt.lb.configurablelexer.anymatch.impl.ConfMatchers.PPM;
import lt.lb.configurablelexer.anymatch.impl.SimplePosMatcherCombinator;
import lt.lb.configurablelexer.anymatch.impl.SimpleStringPosMatcherCombinator;
import lt.lb.configurablelexer.lexer.SimpleLexerOptimized;
import lt.lb.configurablelexer.lexer.matchers.FloatMatcher;
import lt.lb.configurablelexer.lexer.matchers.IntegerMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.lexer.matchers.KeywordMatcher;
import lt.lb.configurablelexer.lexer.matchers.RegexMatcher;
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

        URL resource = MAINParse01.class.getResource("/parse_text_expression.txt");

        BufferedReader input = Files.newBufferedReader(Paths.get(resource.toURI()), StandardCharsets.UTF_8);

        DefaultConfTokenizer<ConfToken> tokenizer = new DefaultConfTokenizer();

        LineAwareCharListener lineListener = new LineAwareCharListener();

        tokenizer.getConfCallbacks()
                .setTokenCharPredicate(
                        new ConfCharPredicate().disallowWhen(Character::isWhitespace)
                )
                .addListener(lineListener);

        SimpleLexerOptimized lexer = tokenizer.getConfCallbacks().nest(t -> new SimpleLexerOptimized<ConfToken>(t) {
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

        ConfTokenizer<ConfToken> myTokenizer = tokenizer;
        myTokenizer.reset(input);
        ConfMatchers M = new ConfMatchers();
        PM any = M.makeNew("ANY").importance(-1).any(1);
        PM number = M.ofType(NumberToken.class);
        PM op = M.ofType(OperatorToken.class);
        PM type = M.or(M.exact("int"), M.exact("float"));
        PM identifier = M.ofType(IdentifierToken.class);
        PM eq = M.exact("=");
        PM variable = M.makeNew("var").or(number, identifier);

        PM expMid = M.makeNew("exp").concat(variable, op, number);
        PM expEnd = M.makeNew("exp end").repeating(true).concat(op, variable);
        PM end = M.makeNew("end").concat(op, variable, M.exact(";"));

        PM arrayStart = M.makeNew("array start").exact("[");
        PM arrayStart1 = M.makeNew("array start 1").concat(arrayStart, variable);
        PM arrayCont = M.makeNew("arrayCont").repeating(true).concat(M.exact(","), variable);

        PM arrayEnd = M.makeNew("array end").exact("]");
        PM assignment = M.makeNew("assigment").concat(type, identifier, eq);

        PM identifierSequence = M.makeNew("Identifier seq").repeating(true).or(identifier);

        PPM array = M.makeNew("array").concatLiftedNames(arrayStart1, arrayCont, arrayEnd);
        PPM emptyArray = M.makeNew("empty array").concatLiftedNames(arrayStart, arrayEnd);
        PPM exp = M.makeNew("full exp").concatLiftedNames(expMid, expEnd);

        List<PM> asList = Arrays.asList(identifierSequence, assignment, expMid, expEnd, end, any, arrayStart, arrayStart1, arrayCont, arrayEnd);
        myTokenizer.reset(input);
        Iterator<PosMatched<ConfToken, String>> matching = SimplePosMatcherCombinator.matching(true, myTokenizer.toSimplifiedIterator().iterator(), asList);

        Iterator<PosMatched<ConfToken, String>> flatLift = SimplePosMatcherCombinator.flatLift(true, matching, Arrays.asList(array, emptyArray, exp));

        StringBuilder sb = new StringBuilder();

        while (flatLift.hasNext()) {
            sb.append(flatLift.next()).append("\n");
        }
        System.out.println(sb);

        input.close();

    }
}
