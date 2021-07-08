package lt.lb.configurablelexer.parse;

import java.util.ArrayList;
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

/**
 *
 * @author laim0nas100
 */
public class DefaultMatchedTokenProducer implements MatchedTokenProducer {

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

    protected List<TokenMatcher> matchers;
    protected ConfTokenizer<ConfToken> tokenizer;
    protected ConfToken localToken = null;
    protected String error;

    public DefaultMatchedTokenProducer(ConfTokenizer<ConfToken> tokenizer, Collection<TokenMatcher> matchers) {
        Objects.requireNonNull(matchers);
        this.tokenizer = Objects.requireNonNull(tokenizer);

        this.matchers = matchers.stream()
                .filter(p -> p.length() > 0)
                .sorted(cmpMatchers)
                .collect(Collectors.toList());
    }

    public boolean hasError(){
        return error != null;
    }
    
    public String getError(){
        return error;
    }
    
    public boolean hasNext() throws Exception {
        if(error != null){
            return false;
        }
        if (localToken != null || tokenizer.hasNextBufferedItem()) {
            return true;
        } else {

            boolean read = tokenizer.readToBuffer();
            if (read) {
                localToken = tokenizer.getCurrentBufferedItem();
            }
            return read;
        }
    }

    protected Optional<MatchedTokens> getNextMatchedToken() throws Exception {
        if (hasNext()) {
            LinkedList<TokenMatcher> toCheck = new LinkedList<>(matchers);
            LinkedList<ConfToken> tempTokens = new LinkedList<>();
            HashMap<Integer, List<TokenMatcher>> finalized = new HashMap<>();
            while ((hasNext()) && !toCheck.isEmpty()) {
                ConfToken next = null;
                if (localToken == null) {
                    next = tokenizer.getNextBufferedItem();
                    localToken = next;

                } else {
                    next = localToken;
                }
                final ConfToken token = next;
                tempTokens.add(token);

                int size = tempTokens.size();
                int localPos = size - 1;
                Iterator<TokenMatcher> iterator = toCheck.iterator();
                boolean foundApplicable = false;
                List<TokenMatcher> exact = new ArrayList<>();
                while (iterator.hasNext()) {
                    TokenMatcher m = iterator.next();
                    if (m.length() >= size && m.requiredType(localPos).isInstance(token) && m.matches(localPos, token)) {
                        if (m.length() == size) {
                            exact.add(m);
                        }
                        foundApplicable = true;
                    } else {
                        iterator.remove();
                    }
                }
                if (foundApplicable) {
                    localToken = null;
                }
                if (!exact.isEmpty()) {
                    finalized.computeIfAbsent(size, c -> new ArrayList<>()).addAll(exact);
                }

            }
            if (finalized.isEmpty()) {

                return Optional.empty();
//                String err = tempTokens + "";
//                if (tempTokens.isEmpty() && localToken != null) {
//                    err = localToken.toString();
//                }
//                
//                throw new MatchedTokenProducerException("Failed to match any matchers, for token " + err);
            }

            Map.Entry<Integer, List<TokenMatcher>> get = finalized.entrySet().stream()
                    .sorted(compEntry).findFirst().get();
            Integer size = get.getKey();
            List<TokenMatcher> maxMatched = get.getValue();
            MatchedTokens matchedTokens = new MatchedTokens(maxMatched, tempTokens.stream().limit(size).collect(Collectors.toList()));
            return Optional.of(matchedTokens);
        }
        return Optional.empty();
    }

    @Override
    public MatchedTokenProducer withTokenizer(ConfTokenizer<ConfToken> lexer) {
        return new DefaultMatchedTokenProducer(lexer, matchers);
    }
}
