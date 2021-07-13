package lt.lb.configurablelexer.parse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lt.lb.configurablelexer.token.ConfToken;
import lt.lb.configurablelexer.token.ConfTokenizer;
import lt.lb.configurablelexer.utils.BufferedIteratorList;
import lt.lb.configurablelexer.utils.RefillableBuffer;

/**
 *
 * @author laim0nas100
 */
public class DefaultMatchedTokenProducer<T extends ConfToken> extends BufferedIteratorList<MatchedTokens<T>> implements MatchedTokenProducer<T> {

    static final Comparator<Map.Entry<Integer, List<TokenMatcher>>> compEntry = new Comparator<Map.Entry<Integer, List<TokenMatcher>>>() {
        @Override
        public int compare(Map.Entry<Integer, List<TokenMatcher>> o1, Map.Entry<Integer, List<TokenMatcher>> o2) {
            return Integer.compare(o1.getKey(), o2.getKey());
        }
    }.reversed();

    static final Comparator<TokenMatcher> compLength = new Comparator<TokenMatcher>() {
        @Override
        public int compare(TokenMatcher o1, TokenMatcher o2) {
            return Integer.compare(o1.length(), o2.length());
        }
    }.reversed();

    static final Comparator<TokenMatcher> compImportance = new Comparator<TokenMatcher>() {
        @Override
        public int compare(TokenMatcher o1, TokenMatcher o2) {
            return Integer.compare(o1.importance(), o2.importance());
        }
    }.reversed();

    static final Comparator<TokenMatcher> cmpMatchers = compLength.thenComparing(compImportance);

    protected List<TokenMatcher<T>> matchers;
    protected ConfTokenizer<T> tokenizer;
    protected RefillableBuffer<T> refillable;

    public DefaultMatchedTokenProducer(ConfTokenizer<T> tokenizer, Collection<TokenMatcher<T>> matchers) {
        Objects.requireNonNull(matchers);
        this.tokenizer = Objects.requireNonNull(tokenizer);
        this.refillable = new RefillableBuffer<>(tokenizer.toSimplifiedIterator().iterator());

        this.matchers = matchers.stream()
                .filter(p -> p.length() > 0)
                .sorted(cmpMatchers)
                .collect(Collectors.toList());
    }

    @Override
    public MatchedTokenProducer<T> withTokenizer(ConfTokenizer<T> lexer) {
        return new DefaultMatchedTokenProducer<>(lexer, matchers);
    }

    public static <T extends ConfToken> List<MatchedTokens<T>> tryMatchAll(Iterator<T> lexer, Collection<TokenMatcher<T>> matchersCol) throws MatchedTokenProducerException {

        List<TokenMatcher<T>> matchers = matchersCol.stream()
                .filter(p -> p.length() > 0)
                .sorted(cmpMatchers).collect(Collectors.toList());

        RefillableBuffer<T> refillable = new RefillableBuffer<>(lexer);
        ArrayList<MatchedTokens<T>> matched = new ArrayList<>();
        Optional<MatchedTokens<T>> findBestMatch = findBestMatch(refillable, matchers);

        while (findBestMatch.isPresent()) {
            matched.add(findBestMatch.get());
            findBestMatch = findBestMatch(refillable, matchers);
        }
        return matched;

    }

    public static <T extends ConfToken> Optional<MatchedTokens<T>> findBestMatch(RefillableBuffer<T> refillable, Collection<TokenMatcher<T>> matchers) throws MatchedTokenProducerException {

        while (refillable.hasNext()) {
            ArrayList<T> liveList = new ArrayList<>();
            LinkedList<TokenMatcher<T>> toCheck = new LinkedList<>(matchers);
            HashMap<Integer, List<TokenMatcher>> finalized = new HashMap<>();
            boolean doMore = true;
            while (doMore && refillable.hasNext()) {
                liveList.add(refillable.next());
                final int size = liveList.size();
                final int localPos = size - 1;
                ConfToken token = liveList.get(localPos);
                Iterator<TokenMatcher<T>> iterator = toCheck.iterator();
                while (iterator.hasNext()) {
                    TokenMatcher m = iterator.next();
                    boolean rep = m.isRepeading();
                    int len = m.length();
                    boolean sizeOk = rep ? true : len >= size;
                    int pos = rep ? localPos % len : localPos;
                    boolean matches = false;
                    if (sizeOk) {
                        Class requiredType = m.requiredType(pos);
                        boolean typeOk = requiredType.isInstance(token);
                        matches = typeOk && m.matches(pos, token);
                    }

                    if (matches) {
                        if (!rep && len == size) {
                            finalized.computeIfAbsent(size, c -> new ArrayList<>()).add(m);
                        } else if (rep && size % len == 0) {
                            finalized.computeIfAbsent(size, c -> new ArrayList<>()).add(m);
                        }

                    } else {
                        iterator.remove();
                    }

                }
                doMore = !toCheck.isEmpty();

            }

            if (finalized.isEmpty()) {
                String err = liveList + "";
                if (liveList.isEmpty() && !liveList.isEmpty()) {
                    err = liveList.toString();
                }
                throw new MatchedTokenProducerException("Failed to match any matchers, for token " + err);
            }

            Map.Entry<Integer, List<TokenMatcher>> get = finalized.entrySet().stream()
                    .sorted(compEntry).findFirst().get();
            List<TokenMatcher> maxMatched = get.getValue();

            ArrayList<ConfToken> tokens = new ArrayList<>(get.getKey());

            int s = get.getKey();
            for (int i = 0; i < s; i++) {
                tokens.add(liveList.get(i));
            }
            for (int i = liveList.size() - 1; i >= s; i--) {
                refillable.returnItem(liveList.get(i));
            }
            return Optional.of(new MatchedTokens(maxMatched, tokens));

        }
        return Optional.empty();

    }

    @Override
    protected Optional<List<MatchedTokens<T>>> produceNextList(Optional<List<MatchedTokens<T>>> currentList) throws MatchedTokenProducerException {
        return findBestMatch(refillable, matchers).map(m -> Arrays.asList(m));// if we cant match, dont try anymore even if more tokens left
    }

}
