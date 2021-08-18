package lt.lb.configurablelexer.lexer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lt.lb.configurablelexer.lexer.matchers.Match;
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
public abstract class SimpleLexerOptimized<T extends ConfToken> extends SimpleLexer<T> {

    public SimpleLexerOptimized(TokenizerCallbacks<T> tokenizer) {
        super(tokenizer);
    }

    public static Optional<MatcherMatch> optimizedFind(List<MatcherMatch> collected, String string, int from, int localLen) {

        for (MatcherMatch mm : collected) {
            if (mm.matcher.minSize() > localLen) {
                continue;
            }
            if (!mm.matcher.canBeBreaking()) {
                continue;
            }
            if (!mm.match.isPostive()) {
                continue;
            }

            if (mm.match instanceof PartialMatch) {
                PartialMatch match = (PartialMatch) mm.match;
                if (match.isBreaking()) {
                    int len = match.to - match.from;
                    if (match.from == from && len == localLen) {
                        return Optional.of(mm);
                    }
                }
            }
            Match match = mm.matcher.match(string, from, localLen);
            if (!match.isBreaking() || !match.isPostive()) {
                continue;
            }

            // try combine
            return Optional.of(new MatcherMatch(mm.matcher, match));
        }
        return Optional.empty();
    }

    @Override
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
            ArrayList<MatcherMatch> collect = new ArrayList<>(matchers.size());
            for (StringMatcher sm : matchers) {
                if (sm.minSize() <= localLen) {
                    Match match = sm.match(string, localOffset, localLen);
                    if (match.isPostive()) {
                        collect.add(new MatcherMatch(sm, match));
                    }
                }
            }

            if (collect.isEmpty()) {
                lexemes.add(makeLiteral(localOffset, last, string));
                moreMatches = false;
            } else { // have matches
                collect.sort(MatcherMatch.cmpMatcherMatch);
                MatcherMatch first = collect.get(0);
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
                        Optional<MatcherMatch> firstBreaking = optimizedFind(collect, string, p.to, tempLocalLen);
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

                                    Optional<MatcherMatch> startingSliceFull = optimizedFind(collect, string, localOffset, startingSliceLen);

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
}
