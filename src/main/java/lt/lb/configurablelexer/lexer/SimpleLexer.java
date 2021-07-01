package lt.lb.configurablelexer.lexer;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lt.lb.configurablelexer.lexer.StringMatcher.Match;
import lt.lb.configurablelexer.lexer.StringMatcher.MatcherMatch;
import lt.lb.configurablelexer.lexer.StringMatcher.PartialMatch;
import lt.lb.configurablelexer.lexer.StringMatcher.PositiveMatch;
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

        int last = string.length();
        ArrayList<T> lexemes = new ArrayList<>();

        int processed = 0;

        //TODO
        // NEW IDEA - find first full, if not found 
        // find first breaking. Break the string into 2 or 3 parts
        // if breaking is in the start - all good, continue with smaller string
        // if breaking is not in the start split the string [start][breaking_lexeme][the_rest]
        // [the_rest] can also be empty.
        // try to match [start], if failed, it becomes a literal.
        boolean moreMatches = true;
        List<MatcherMatch> matchDept = new ArrayList<>();
        int debtProcessed = 0;

        while (moreMatches && processed < last) {
            final int localLen = last - processed;
            final int localOffset = processed;
            List<MatcherMatch> optMatch = matchers.stream()
                    .filter(m -> m.minSize() >= localLen)
                    .map(m -> new MatcherMatch(m, m.match(string, localOffset, localLen)))
                    .filter(m -> m.match.isPostive())
                    .sorted(MatcherMatch.cmpMatcherMatch)
                    .collect(Collectors.toList());
            boolean needFindBreak = false;
            if (optMatch.isEmpty()) { // no more matches
                lexemes.add(makeLiteral(processed, last, string));
                moreMatches = false;
            } else { // matched
                MatcherMatch optMatcherMatch = optMatch.get(0);
                PositiveMatch match = (PositiveMatch) optMatcherMatch.match;

                if (match.isBreaking()) { // we can split tokens easily
                    if (match.isFull()) {
                        lexemes.add(makeLexeme(processed, last, optMatcherMatch, string));
                        moreMatches = false;
                    } else if (match.isPartial()) {
                        PartialMatch pMatch = (PartialMatch) match;
                        if (pMatch.from == processed) {//matched at the start, easy path
                            lexemes.add(makeLexeme(processed, pMatch.to, optMatcherMatch, string));
                            processed += pMatch.size();
                        } else { // have leading chars

                        }
                    } else {
                        throw new IllegalStateException("Unrecognized match type: " + match);
                    }
                } else {
                    if (matchDept.isEmpty()) {
                        if (match.isFull()) { // no chance for breaks.
                            lexemes.add(makeLexeme(processed, last, optMatcherMatch, string));
                            moreMatches = false;
                        } else if (match.isPartial()) {
                            PartialMatch pMatch = (PartialMatch) match;
                            if (pMatch.from == processed) { //matched at the start
                                
                                matchDept.add(optMatcherMatch);
                                processed += pMatch.size();
                                debtProcessed += pMatch.size(); // make debt
                            } else if (pMatch.from > processed) { // have leading chars, not good
                                needFindBreak = true;
                            } else {
                                throw new IllegalStateException("Partial match out of bounds processed:" + processed + " returned from:" + pMatch.from);
                            }
                        } else {
                            throw new IllegalStateException("Unrecognized match type: " + match);
                        }
                    } 
                    if(needFindBreak || !matchDept.isEmpty()){
                        
                        
                        
                        // we have dept, only way to resolve is to find breaking
                        Optional<MatcherMatch> findFirst = optMatch.stream().filter(f -> f.match.isBreaking()).findFirst();
                        if(!findFirst.isPresent()){
                            //no breaks, this is a literal then
                            
                        }else{
                            MatcherMatch get = findFirst.get(); // first breaking. Solve when we have chars before
                        }
                    }

                }
            }

        }

        return ConfTokenBuffer.ofList(lexemes);
    }

    public abstract T makeLexeme(int form, int to, MatcherMatch matcher, String str) throws Exception;

    public abstract T makeLiteral(int from, int to, String str) throws Exception;

    @Override
    public ConfTokenizer<T> getDelegate() {
        return tokenizer;
    }

}
