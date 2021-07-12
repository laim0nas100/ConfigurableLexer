package lt.lb.configurablelexer.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lt.lb.configurablelexer.Id;
import lt.lb.configurablelexer.token.ConfToken;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author laim0nas100
 */
public class MatchedTokens<T extends ConfToken> implements Id {

    public final List<TokenMatcher<T>> matchedBy;
    public final List<T> tokens;

    public MatchedTokens(List<TokenMatcher<T>> matched, List<T> tokens) {

        if (matched == null || matched.isEmpty()) {
            throw new IllegalArgumentException("Empty matched");
        }
        if (tokens == null || tokens.isEmpty()) {
            throw new IllegalArgumentException("Empty tokens");
        }
        this.matchedBy = matched;
        this.tokens = tokens;
    }

    public TokenMatcher firstMatch() {
        return matchedBy.get(0);
    }

    public ConfToken getToken(int index) {
        return tokens.get(index);
    }

    public List<ConfToken> getTokens(int... index) {
        List<ConfToken> list = new ArrayList<>(index.length);
        for (int i = 0; i < index.length; i++) {
            list.add(tokens.get(index[i]));
        }
        return list;
    }

    public boolean containsName(String name) {
        return matchedBy.stream().map(m -> m.name()).anyMatch(n -> StringUtils.equals(n, name));
    }

    public boolean contains(TokenMatcher matcher) {
        return matchedBy.contains(matcher);
    }

    @Override
    public String toString() {
        return descriptiveString();
    }

    public String names() {
        return "" + matchedBy.stream().map(m -> m.name()).collect(Collectors.toList());
    }

    public String values() {
        return "" + tokens.stream().map(m -> m.getValue()).collect(Collectors.toList());
    }

    public int count() {
        return tokens.size();
    }

    @Override
    public String stringValues() {
        return "matchedBy=" + names() + ", tokens=" + values();
    }

}
