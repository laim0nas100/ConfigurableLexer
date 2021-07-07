package lt.lb.configurablelexer;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import lt.lb.commons.DLog;
import lt.lb.configurablelexer.lexer.SimpleLexer;
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
import lt.lb.configurablelexer.token.simple.Pos;
import lt.lb.configurablelexer.token.spec.comment.LineCommentAwareCallback;
import lt.lb.configurablelexer.token.spec.comment.LineCommentAwareCallbackString;
import lt.lb.configurablelexer.token.spec.comment.MultilineCommentAwareCallback;
import lt.lb.configurablelexer.token.spec.comment.PositionAwareCommentCallback;

/**
 *
 * @author laim0nas100
 */
public class MAINText05 {

    public static void main(String[] args) throws Exception {
        DLog main = DLog.main();
        main.async = false;
        main.stackTrace = false;
        main.surroundString = false;
        main.threadName = false;
        DLog.useTimeFormat(main, "HH:mm:ss.SSS ");
        Reader input = new FileReader(new File("parse_text.txt"), StandardCharsets.UTF_8);

        ConfTokenizerCallbacks callbacks = new ConfTokenizerCallbacks<>().setTokenCharPredicate(
                new ConfCharPredicate().disallowWhen(Character::isWhitespace)
        );

        LineAwareCharListener lineListener = new LineAwareCharListener();

        callbacks.addListener(lineListener);

        PositionAwareCommentCallback<ConfToken, Pos> commentCallback = new PositionAwareCommentCallback<ConfToken, Pos>(callbacks) {
            @Override
            public Pos getPosition() {
                return lineListener.getPos();
            }

            @Override
            public ConfToken contructComment(Pos start, Pos end, char[] buffer, int offset, int length) throws Exception {
                return new CommentToken(String.valueOf(buffer, offset, length), start);
            }
        }
                .enableLineComment('#', '$')
                .enableLineComment("//")
                .enableMultilineComment("/*", "*/");

        callbacks.nest(t->commentCallback);

        BaseTokenizer tokenizer_with_comments = new BaseTokenizer() {

            @Override
            protected TokenizerCallbacks getCallbacks() {
                return callbacks;
            }
        };

        SimpleLexer lexer = new SimpleLexer(tokenizer_with_comments) {
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

        ConfTokenizer myTokenizer = lexer;
        myTokenizer.reset(input);
        myTokenizer.produceTokens(t -> {
            DLog.print(t);
        });
        input.close();

        DLog.close();
    }
}
