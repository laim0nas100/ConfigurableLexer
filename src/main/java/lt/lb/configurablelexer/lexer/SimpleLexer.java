package lt.lb.configurablelexer.lexer;

import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import lt.lb.configurablelexer.token.CharListener;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.ConfTokenizer;

/**
 *
 * @author laim0nas100
 * @param <T>
 */
public abstract class SimpleLexer<T extends ConfToken> implements Lexer<T> {

    protected List<CharListener> charListeners = new ArrayList<>();
    protected ConfTokenizer<T> tokenizer;
    protected List<StringMatcher> matchers = new ArrayList<>();

    public SimpleLexer(ConfTokenizer<T> tokenizer) {
        this.tokenizer = Objects.requireNonNull(tokenizer);
    }

    @Override
    public void reset(Reader input) {
        getDelegate().reset(input);
        for (CharListener listener : charListeners) {
            listener.reset();
        }
    }

    @Override
    public void charListener(boolean isTokenChar, int c) {
        getDelegate().charListener(isTokenChar, c);
        for (CharListener listener : charListeners) {
            listener.listen(isTokenChar, c);
        }
    }

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return constructLexemes(buffer, offset, length);
    }

    public ConfTokenBuffer<T> constructLexemes(char[] buffer, int offset, int length) throws Exception {
       
        String string = String.valueOf(buffer, offset, length);
        
        // try to match the whole thing
        
        int len = string.length();
        for(StringMatcher matcher:this.matchers){
            if(matcher.match(string, 0, len).isFull()){
                return ConfTokenBuffer.of(makeLexeme(matcher, string));
            }
        }
        
         ArrayList<T> lexemes = new ArrayList<>();
        
        int processed = 0;
        int local = 0;

        while (true) {
            List<StringMatcher> applicable = new ArrayList<>(this.matchers);
            local++;
            int from = processed;
            int to = processed + local;
//            String substring = string.substring(processed, processed+local);
            Iterator<StringMatcher> applicableIterator = applicable.iterator();
            List<StringMatcher> stillGood = new ArrayList<>();
            while (applicableIterator.hasNext()) {
                StringMatcher next = applicableIterator.next();

                if (next.match(string, from, to)) {
                    stillGood.add(next);
                }
                applicableIterator.remove();

            }

        }
        return null;
    }

    public abstract T makeLexeme(StringMatcher matcher, String str) throws Exception;

    public abstract T makeLiteral(StringMatcher matcher, String str) throws Exception;

    @Override
    public ConfTokenizer<T> getDelegate() {
        return tokenizer;
    }

}
