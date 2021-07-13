package test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import lt.lb.configurablelexer.Redirecter;
import lt.lb.configurablelexer.lexer.SimpleLexer;
import lt.lb.configurablelexer.lexer.matchers.FloatMatcher;
import lt.lb.configurablelexer.lexer.matchers.IntegerMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.lexer.matchers.KeywordMatcher;
import lt.lb.configurablelexer.token.base.KeywordToken;
import lt.lb.configurablelexer.token.base.BaseStringToken;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.CharInfo;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.ConfTokenizerCallbacks;
import lt.lb.configurablelexer.token.spec.LineAwareCharListener;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.base.CommentToken;
import lt.lb.configurablelexer.token.base.LiteralToken;
import lt.lb.configurablelexer.token.base.NumberToken;
import lt.lb.configurablelexer.token.simple.Pos;

/**
 *
 * @author laim0nas100
 */
public class MAINText01 {

    public static void main(String[] args) throws Exception {
        URL resource = Redirecter.class.getResource("/parse_text.txt");
        Reader input = new FileReader(resource.getFile(), StandardCharsets.UTF_8);

        ConfTokenizerCallbacks callbacks = new ConfTokenizerCallbacks<>();
        ConfCharPredicate tokenPred = new ConfCharPredicate();
        tokenPred.disallowWhen(Character::isWhitespace);

        callbacks.setTokenCharPredicate(tokenPred);

        LineAwareCharListener lineListener = new LineAwareCharListener();

        callbacks.addListener(lineListener);

        BaseTokenizer tokenizer_with_comments = new BaseTokenizer() {
            boolean inLineComment;
            boolean constructComment;

            @Override
            protected TokenizerCallbacks getCallbacks() {
                return callbacks;
            }

            @Override
            public void charListener(CharInfo chInfo, int c) {
                if (inLineComment) {
                    if (c == '\n') {
                        inLineComment = false;
                        constructComment = true;
                    }
                } else if (c == '#') {
                    if (!inLineComment) {
                        inLineComment = true;
                    }

                }

                super.charListener(chInfo, c);
            }

            @Override
            public boolean isTokenChar(int c) {
                if (inLineComment) {
                    return c != '\n';
                }
                return super.isTokenChar(c);
            }

            @Override
            public ConfTokenBuffer constructTokens(char[] buffer, int offset, int length) throws Exception {
                if (constructComment) {
                    constructComment = false;
                    return ConfTokenBuffer.of(new CommentToken<>(String.valueOf(buffer, offset, length), lineListener.getPos(offset, length)));

                }
                return super.constructTokens(buffer, offset, length);
            }

            @Override
            public boolean isBreakChar(int c) {
                if (!inLineComment) {
                    if (c == '#') {
                        return true;
                    }
                }
                return super.isBreakChar(c);
            }

        };

        SimpleLexer lexer = new SimpleLexer(tokenizer_with_comments) {
            @Override
            public BaseStringToken<Pos> makeLexeme(int from, int to, StringMatcher.MatcherMatch matcher, String str) throws Exception {
                Pos pos = new Pos(lineListener.getLine() + 1, from + lineListener.getColumn() - str.length());
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
                Pos pos = new Pos(lineListener.getLine() + 1, from + lineListener.getColumn() - str.length());
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
        StringBuilder sb = new StringBuilder();
        myTokenizer.produceItems(t -> {
            sb.append(t).append("\n");
        });
        System.out.println(sb);
        input.close();

    }
}
