package lt.lb.configurablelexer.lexer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lt.lb.configurablelexer.lexer.matchers.Match.PartialMatch;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher;
import lt.lb.configurablelexer.lexer.matchers.StringMatcher.MatcherMatch;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenBuffer;
import lt.lb.configurablelexer.token.DelegatingTokenizerCallbacks;
import lt.lb.configurablelexer.token.TokenizerCallbacks;

/**
 *
 * @author laim0nas100
 * @param <T>
 */
public abstract class SimpleLexer<T extends ConfToken> implements Lexer<T>, DelegatingTokenizerCallbacks<T> {

    protected TokenizerCallbacks<T> tokenizer;
    protected List<StringMatcher> matchers = new ArrayList<>();

    public SimpleLexer(TokenizerCallbacks<T> tokenizer) {
        this.tokenizer = Objects.requireNonNull(tokenizer);
    }

    public void addMatcher(StringMatcher matcher) {
        matchers.add(matcher);
    }

    @Override
    public ConfTokenBuffer<T> constructTokens(char[] buffer, int offset, int length) throws Exception {
        return constructLexemes(buffer, offset, length);
    }

    public ConfTokenBuffer<T> constructLexemes(char[] buffer, int offset, int length) throws Exception {

        String string = String.valueOf(buffer, offset, length);

        // Find first full, if not found 
        // find first breaking. Break the string into 2 or 3 parts
        // if breaking is in the start - all good, continue with smaller string
        // if breaking is not in the start split the string [start][breaking_lexeme][the_rest]
        // [the_rest] can also be empty.
        // try to match [start], if failed, it becomes a literal.
        int last = string.length();
        ArrayList<T> lexemes = new ArrayList<>();
        int processed = 0;
        boolean moreMatches = true;

        while (moreMatches && processed < last) {
            final int localLen = last - processed;
            final int localOffset = processed;
            Optional<MatcherMatch> optMatch = matchers.stream()
                    .filter(m -> m.minSize() <= localLen)
                    .map(m -> new MatcherMatch(m, m.match(string, localOffset, localLen)))
                    .filter(m -> m.match.isPostive())
                    .sorted(MatcherMatch.cmpMatcherMatch)
                    .findFirst();

            if (!optMatch.isPresent()) {
                lexemes.add(makeLiteral(localOffset, last, string));
                moreMatches = false;
            } else { // have matches

                MatcherMatch first = optMatch.get();
                if (first.match.isFull()) {// best case
                    lexemes.add(makeLexeme(localOffset, last, first, string));
                    moreMatches = false;

                } else if (first.match.isPartial()) {
                    PartialMatch p = (PartialMatch) first.match;
                    if (first.match.isBreaking()) {
                        //can be a literal, if not empty
                        if (p.from > localOffset) {
                            lexemes.add(makeLiteral(localOffset, p.from, string));
                        }
                        lexemes.add(makeLexeme(p.from, p.to, first, string));
                        processed = p.to;
                    } else {

                        final int tempLocalLen = last - p.to;
                        Optional<MatcherMatch> firstBreaking = matchers.stream()
                                .filter(m -> m.minSize() <= tempLocalLen)
                                .filter(m -> m.canBeBreaking())
                                .map(m -> new MatcherMatch(m, m.match(string, p.to, tempLocalLen)))
                                .filter(m -> m.match.isPostive() && m.match.isBreaking())
                                .sorted(MatcherMatch.cmpMatcherMatch)
                                .findFirst();
                        if (firstBreaking.isPresent()) {
                            MatcherMatch get = firstBreaking.get();
                            if (get.match.isFull()) {
                                lexemes.add(makeLexeme(p.from, p.to, first, string));
                                lexemes.add(makeLexeme(p.to, last, get, string));
                                moreMatches = false;
                            } else if (get.match.isPartial()) {

                                PartialMatch p2 = (PartialMatch) get.match;
                                if (p2.from == p.to) { // exact cut mark
                                    lexemes.add(makeLexeme(p.from, p.to, first, string));
                                    lexemes.add(makeLexeme(p.to, p2.to, get, string));
                                    processed = p2.to;
                                } else {// inexact cut mark
                                    final int startingSliceLen = p2.from - processed;
                                    Optional<MatcherMatch> startingSliceFull = matchers.stream()
                                            .filter(m -> m.minSize() >= startingSliceLen)
                                            .map(m -> new MatcherMatch(m, m.match(string, localOffset, startingSliceLen)))
                                            .filter(m -> m.match.isPostive() && m.match.isFull())
                                            .sorted(MatcherMatch.cmpMatcherMatch)
                                            .findFirst();

                                    if (startingSliceFull.isPresent()) {
                                        MatcherMatch slice = startingSliceFull.get();
                                        lexemes.add(makeLexeme(localOffset, localOffset + startingSliceLen, slice, string));

                                    } else {// just make literal
                                        lexemes.add(makeLiteral(localOffset, localOffset + startingSliceLen, string));
                                    }
                                    lexemes.add(makeLexeme(p2.from, p2.to, get, string));
                                    processed = p2.to;
                                }
                            } else {
                                throw new IllegalArgumentException("Unsupported matcher " + get);
                            }
                        } else {// not possible to break, treat as literal
                            lexemes.add(makeLiteral(processed, last, string));
                            moreMatches = false;
                        }
                    }
                }

            }
        }

        return ConfTokenBuffer.ofList(lexemes);
    }

    public abstract T makeLexeme(int from, int to, MatcherMatch matcher, String unbrokenString) throws Exception;

    public abstract T makeLiteral(int from, int to, String unbrokenString) throws Exception;

    @Override
    public TokenizerCallbacks<T> delegate() {
        return tokenizer;
    }

    public List<StringMatcher> getMatchers() {
        return matchers;
    }

    public void setMatchers(List<StringMatcher> matchers) {
        this.matchers = matchers;
    }

}
