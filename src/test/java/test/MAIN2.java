package test;

import java.util.regex.Pattern;
import lt.lb.configurablelexer.lexer.SimpleLexer;
import lt.lb.configurablelexer.lexer.SimpleLexerOptimized;
import lt.lb.configurablelexer.lexer.matchers.FloatMatcher;
import lt.lb.configurablelexer.lexer.matchers.IntegerMatcher;
import lt.lb.configurablelexer.lexer.matchers.KeywordMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.token.BaseTokenizer;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.token.ConfTokenizerCallbacks;
import lt.lb.configurablelexer.token.TokenizerCallbacks;
import lt.lb.configurablelexer.token.base.BaseStringToken;
import lt.lb.configurablelexer.token.base.KeywordToken;
import lt.lb.configurablelexer.token.base.LiteralToken;
import lt.lb.configurablelexer.token.base.NumberToken;
import lt.lb.configurablelexer.token.simple.Pos;
import lt.lb.configurablelexer.token.spec.LineAwareCharListener;

/**
 *
 * @author laim0nas100
 */
public class MAIN2 {

    public static void main(String[] args) throws Exception {
        Pattern compile = Pattern.compile("\\d+\\.\\d+");
        String input = "labas 1+1.10\n"
                + "int i = 10++;\n"
                + "ok\n";

        ConfTokenizerCallbacks callbacks = new ConfTokenizerCallbacks<>();

        BaseTokenizer tokenizer = new BaseTokenizer() {
            @Override
            protected TokenizerCallbacks getCallbacks() {
                return callbacks;
            }
        };
        ConfCharPredicate tokenPred = new ConfCharPredicate();
        tokenPred.disallowWhen(Character::isWhitespace);

        callbacks.setTokenCharPredicate(tokenPred);

        LineAwareCharListener lineListener = new LineAwareCharListener();

        callbacks.addListener(lineListener);

        SimpleLexer lexer = new SimpleLexerOptimized(tokenizer) {
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

                LiteralToken<Pos> literalToken = new LiteralToken<>();

                literalToken.setInfo(pos);
                literalToken.setValue(str.substring(from, to));
                return literalToken;
            }
        };

        callbacks.setConstructor(lexer);

        lexer.addMatcher(new KeywordMatcher("labas"));
        lexer.addMatcher(new IntegerMatcher());
        lexer.addMatcher(new FloatMatcher());
        lexer.addMatcher(new KeywordMatcher("+", true));
        lexer.addMatcher(new KeywordMatcher("++", true));

        ConfTokenizer myTokenizer = tokenizer;
        myTokenizer.reset(input);
        StringBuilder sb = new StringBuilder();
        myTokenizer.produceItems(t -> {
           sb.append(t).append("\n");
        });
        
        System.out.println(sb);

    }
}
