package test;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;
import lt.lb.commons.DLog;
import lt.lb.configurablelexer.lexer.SimpleLexer;
import lt.lb.configurablelexer.lexer.matchers.FloatMatcher;
import lt.lb.configurablelexer.lexer.matchers.IntegerMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.lexer.matchers.KeywordMatcher;
import lt.lb.configurablelexer.lexer.matchers.RegexMatcher;
import lt.lb.configurablelexer.parse.DefaultMatchedTokenProducer;
import lt.lb.configurablelexer.parse.MatchedTokens;
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
import lt.lb.configurablelexer.utils.BufferedIterator.SimplifiedBufferedIterator;

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

    public static class IdentiefierToken<Inf> extends BaseStringToken<Inf> {

        public IdentiefierToken() {
        }

        public IdentiefierToken(String value) {
            super(value);
        }

        public IdentiefierToken(String value, Inf info) {
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
        DLog main = DLog.main();
        main.async = false;
        main.stackTrace = false;
        main.surroundString = true;
        main.threadName = false;
        DLog.useTimeFormat(main, "HH:mm:ss.SSS ");
        Reader input = new FileReader(new File("parse_text_expression.txt"), StandardCharsets.UTF_8);

        DefaultConfTokenizer<ConfToken> tokenizer = new DefaultConfTokenizer();

        LineAwareCharListener lineListener = new LineAwareCharListener();

        tokenizer.getCallbacks()
                .setTokenCharPredicate(
                        new ConfCharPredicate().disallowWhen(Character::isWhitespace)
                )
                .addListener(lineListener);

        SimpleLexer lexer = new SimpleLexer(tokenizer) {
            @Override
            public BaseStringToken<Pos> makeLexeme(int from, int to, StringMatcher.MatcherMatch matcher, String str) throws Exception {
                Pos pos = lineListener.getPos(from, str.length());
                String val = str.substring(from, to);
                if (matcher.matcher instanceof KeywordMatcher) {
                    if (matcher.matcher instanceof OperatorMatcher) {
                        return new OperatorToken<>(val,pos);
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
                    return new IdentiefierToken<>(val, pos);
                }
                throw new IllegalArgumentException("Unrecognized matcher");
            }

            @Override
            public BaseStringToken<Pos> makeLiteral(int from, int to, String str) throws Exception {
                Pos pos = lineListener.getPos(from, str.length());
                return new LiteralToken<>(str.substring(from, to), pos);
            }
        };

        tokenizer.getCallbacks().nest(t -> lexer);
        tokenizer.getCallbacks().nest(t -> {
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

        tokenizer.getCallbacks().addListener((info, c) -> {
//            DLog.print(info,Character.toString(c));
        });

        lexer.addMatcher(new KeywordMatcher("labas"));
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

        ConfTokenizer myTokenizer = tokenizer;
        myTokenizer.reset(input);

        List<ConfToken> produceItems = myTokenizer.produceItems();
        for (ConfToken t : produceItems) {
            DLog.print(t);
        }
        DLog.println("\n");
        TokenMatcher<ConfToken> any = TokenMatchers.any(1);
        TokenMatcher<ConfToken> number = TokenMatchers.ofType(NumberToken.class);
        TokenMatcher<ConfToken> op = TokenMatchers.ofType(OperatorToken.class);
        TokenMatcher<ConfToken> key = TokenMatchers.ofType(KeywordToken.class);
        TokenMatcher<ConfToken> type = TokenMatchers.or(TokenMatchers.exact("int"),TokenMatchers.exact("float"));
         TokenMatcher<ConfToken> identifier = TokenMatchers.ofType(IdentiefierToken.class);
         TokenMatcher<ConfToken> eq = TokenMatchers.exact("=");
        TokenMatcher<ConfToken> concat = TokenMatchers.concat(identifier, eq);
        
        TokenMatcher<ConfToken> exprStart = TokenMatchers.concat(number,op).repeating(true).named("exp start");
        TokenMatcher<ConfToken> expMid = TokenMatchers.concat(number,op,number).repeating(true).named("exp");
        TokenMatcher<ConfToken> expEnd = TokenMatchers.concat(op,number).repeating(true).named("exp end");
        
        TokenMatcher<ConfToken> assignment = TokenMatchers.concat(type,identifier,eq).named("Assignment");
        
        input.close();
       input = new FileReader(new File("parse_text_expression.txt"), StandardCharsets.UTF_8);
        myTokenizer.reset(input);
        
        DefaultMatchedTokenProducer defaultMatchedTokenProducer = new DefaultMatchedTokenProducer<>(myTokenizer,Arrays.asList(assignment,exprStart,expMid,expEnd,any));
        
        
        defaultMatchedTokenProducer.toSimplifiedIterator().forEach(m->{
            DLog.print(m);
        });
        
        input.close();
//       input = new FileReader(new File("parse_text_expression.txt"), StandardCharsets.UTF_8);
//        myTokenizer.reset(input);
//        Iterator<ConfToken> iterator = myTokenizer.produceItems().iterator();
//        List<MatchedTokens<ConfToken>> tryMatchAll = DefaultMatchedTokenProducer.tryMatchAll(iterator, Arrays.asList(expr,any));
//        
//        DLog.print("AFTER");
////        List<MatchedTokens> match = DefaultMatchedTokenProducer.tryMatch(produceItems.iterator(), Arrays.asList(expr,any));
//        for (MatchedTokens mt : tryMatchAll) {
//            DLog.print(mt);
//        }
//        input.close();

        DLog.close();
    }
}
