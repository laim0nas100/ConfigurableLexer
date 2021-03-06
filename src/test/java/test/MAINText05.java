package test;

import java.io.BufferedReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import lt.lb.configurablelexer.lexer.SimpleLexer;
import lt.lb.configurablelexer.lexer.SimpleLexerOptimized;
import lt.lb.configurablelexer.lexer.matchers.FloatMatcher;
import lt.lb.configurablelexer.lexer.matchers.IntegerMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.lexer.matchers.KeywordMatcher;
import lt.lb.configurablelexer.token.base.KeywordToken;
import lt.lb.configurablelexer.token.base.BaseStringToken;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.ConfTokenizerCallbacks;
import lt.lb.configurablelexer.token.spec.LineAwareCharListener;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.base.CommentToken;
import lt.lb.configurablelexer.token.base.LiteralToken;
import lt.lb.configurablelexer.token.base.NumberToken;
import lt.lb.configurablelexer.token.base.StringToken;
import lt.lb.configurablelexer.token.simple.Pos;
import lt.lb.configurablelexer.token.spec.ExtendedPositionAwareSplittableCallback;
import lt.lb.configurablelexer.token.spec.PositionAwareDefaultCallback;
import lt.lb.configurablelexer.token.spec.string.StringAwareCallback;
import lt.lb.configurablelexer.token.spec.comment.CommentAwareCallback;

/**
 *
 * @author laim0nas100
 */
public class MAINText05 {

    public static void main(String[] args) throws Exception {
        URL resource = MAINText05.class.getResource("/parse_text.txt");
        BufferedReader input = Files.newBufferedReader(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
        ConfTokenizerCallbacks callbacks = new ConfTokenizerCallbacks<>().setTokenCharPredicate(
                new ConfCharPredicate().disallowWhen(Character::isWhitespace)
        );

        LineAwareCharListener lineListener = new LineAwareCharListener();

        callbacks.addListener(lineListener);

        PositionAwareDefaultCallback<ConfToken, Pos> commentCallback = new PositionAwareDefaultCallback<ConfToken, Pos>(callbacks) {

            @Override
            public Pos getPosition() {
                return lineListener.getPos();
            }

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
                .enableMultilineComment("/*", "*/");

        callbacks.nest(t -> commentCallback);

        BaseTokenizer tokenizer_with_comments = new BaseTokenizer() {

            @Override
            protected TokenizerCallbacks getCallbacks() {
                return callbacks;
            }
        };

        SimpleLexer lexer = new SimpleLexerOptimized(tokenizer_with_comments) {
            @Override
            public BaseStringToken<Pos> makeLexeme(int from, int to, StringMatcher.MatcherMatch matcher, String str) throws Exception {
                Pos pos = lineListener.getPos(from, str.length());
                String val = str.substring(from, to);
                if (matcher.matcher instanceof KeywordMatcher) {
                    return new KeywordToken<>(val, pos);
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
                throw new IllegalArgumentException("Unrecognized matcher");
            }

            @Override
            public BaseStringToken<Pos> makeLiteral(int from, int to, String str) throws Exception {
                Pos pos = lineListener.getPos(from, str.length());
                return new LiteralToken<>(str.substring(from, to), pos);
            }
        };

        callbacks.setConstructor(lexer);

        lexer.addMatcher(new KeywordMatcher("labas"));
        lexer.addMatcher(new IntegerMatcher());
        lexer.addMatcher(new FloatMatcher());
        lexer.addMatcher(new KeywordMatcher("+", true));
        lexer.addMatcher(new KeywordMatcher("++", true));
        lexer.addMatcher(new KeywordMatcher(";", true));

        ConfTokenizer myTokenizer = tokenizer_with_comments;
        myTokenizer.reset(input);
        StringBuilder sb = new StringBuilder();
        myTokenizer.produceItems(t -> {
            sb.append(t).append("\n");
        });
        System.out.println(sb);
        input.close();

    }
}
