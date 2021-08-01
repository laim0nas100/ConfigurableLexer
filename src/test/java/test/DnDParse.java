package test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;
import lt.lb.configurablelexer.lexer.SimpleLexer;
import lt.lb.configurablelexer.lexer.matchers.RegexMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.token.ConfCharPredicate;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.DefaultConfTokenizer;
import lt.lb.configurablelexer.token.base.LiteralToken;
import lt.lb.configurablelexer.token.base.ProcessedToken;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author laim0nas100
 */
public class DnDParse {

    public static class DiceInfo {

        public final int dice;
        public final int value;

        public DiceInfo(int dice, int value) {
            this.dice = dice;
            this.value = value;
        }

        @Override
        public String toString() {
            return "DiceInfo{" + "dice=" + dice + ", value=" + value + '}';
        }

    }

    public static class DiceToken extends ProcessedToken<Object, List<DiceInfo>> {

        public DiceToken() {
        }

        public DiceToken(String value) {
            super(value);
        }

        public DiceToken(String value, Object info) {
            super(value, info);
        }

    }

    public static class DiceMatcher extends RegexMatcher {

        public DiceMatcher() {
            pattern = Pattern.compile("(\\d)*[d,D](\\d+)");
        }

    }
    static Random rng = new Random();

    public static DiceInfo roll(int roll) {
        int rolled = rng.nextInt(roll) + 1;
        return new DiceInfo(roll, rolled);
    }

    public static void main(String[] args) throws Exception {
        DefaultConfTokenizer<ConfToken> tokenizer = new DefaultConfTokenizer();

        tokenizer.getConfCallbacks()
                .setTokenCharPredicate(
                        new ConfCharPredicate().disallowWhen(Character::isWhitespace)
                );

        SimpleLexer lexer = tokenizer.getConfCallbacks().nest(m -> new SimpleLexer<ConfToken>(m) {
            @Override
            public ConfToken makeLexeme(int from, int to, StringMatcher.MatcherMatch matcher, String unbrokenString) throws Exception {
                String str = unbrokenString.substring(from, to);

                if (matcher.matcher instanceof DiceMatcher) {

                    DiceToken diceToken = new DiceToken(str);
                    diceToken.processValue(s -> {
                        String[] split = StringUtils.split(s, "dD");//case sensitive, so test both cases
                        int times = 1;
                        int roll = 20;

                        if (split.length == 2) {
                            times = Integer.parseInt(split[0]);
                            roll = Integer.parseInt(split[1]);
                        } else if (split.length == 1) {
                            roll = Integer.parseInt(split[0]);
                        }
                        List<DiceInfo> list = new ArrayList<>(times);

                        for (int i = 0; i < times; i++) {
                            list.add(roll(roll));
                        }
                        return list;
                    });

                    return diceToken;

                }

                return makeLiteral(from, to, unbrokenString);
            }

            @Override
            public ConfToken makeLiteral(int from, int to, String unbrokenString) throws Exception {
                String str = unbrokenString.substring(from, to);
                return new LiteralToken(str);
            }
        });

        lexer.addMatcher(new DiceMatcher());

        tokenizer.reset("Rolled: 5d20 and D50");
        List<ConfToken> produceItems = tokenizer.produceItems();

        StringBuilder cleanOutput = new StringBuilder();

        for (ConfToken token : produceItems) {
            if (token instanceof DiceToken) {
                DiceToken dt = (DiceToken) token;
                cleanOutput.append(dt.getValue()).append(" [");
                boolean first = true;
                int sum = 0;
                for (DiceInfo di : dt.getProcessedValue()) {
                    if (first) {
                        first = false;
                    } else {
                        cleanOutput.append(", ");
                    }
                    cleanOutput.append(di.value);
                    sum += di.value;

                }
                cleanOutput.append("]=").append(sum);
            } else {
                cleanOutput.append(token.getValue());
            }
            cleanOutput.append(" ");
        }

        System.out.println(cleanOutput);

    }
}
