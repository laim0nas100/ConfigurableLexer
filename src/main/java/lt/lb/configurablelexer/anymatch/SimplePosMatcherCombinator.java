package lt.lb.configurablelexer.anymatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lt.lb.configurablelexer.utils.BufferedIteratorList;
import lt.lb.configurablelexer.utils.RefillableBuffer;

/**
 *
 * @author laim0nas100
 * @param <T>
 * @param <I> serialized name
 * @param <P>
 */
public class SimplePosMatcherCombinator<T, I, P extends PosMatch<T, I>> extends BufferedIteratorList<PosMatched<T, I>> {

    public static class MatchException extends Exception {

        public MatchException() {
        }

        public MatchException(String message) {
            super(message);
        }

    }

    public static final Comparator<PosMatch> compLength = new Comparator<PosMatch>() {
        @Override
        public int compare(PosMatch o1, PosMatch o2) {
            return Integer.compare(o1.getLength(), o2.getLength());
        }
    }.reversed();

    public static final Comparator<PosMatch> compImportance = new Comparator<PosMatch>() {
        @Override
        public int compare(PosMatch o1, PosMatch o2) {
            return Integer.compare(o1.getImportance(), o2.getImportance());
        }
    }.reversed();

    public static final Comparator<PosMatch> cmpMatchers = compLength.thenComparing(compImportance);

    public static <T, I, P extends PosMatch<T, I>> Optional<PosMatched<T, I>> findBestMatch(RefillableBuffer<T> refillable, Collection<? extends P> matchers) throws MatchException {

        while (refillable.hasNext()) {
            ArrayList<T> liveList = new ArrayList<>();
            LinkedList<P> toCheck = new LinkedList<>(matchers);
            HashMap<Integer, List<I>> finalized = new HashMap<>();
            boolean doMore = true;
            int max = -1;
            while (doMore && refillable.hasNext()) {
                liveList.add(refillable.next());
                final int size = liveList.size();
                final int localPos = size - 1;
                T token = liveList.get(localPos);
                Iterator<P> iterator = toCheck.iterator();
                while (iterator.hasNext()) {
                    P m = iterator.next();
                    boolean rep = m.isRepeating();
                    int len = m.getLength();
                    boolean sizeOk = rep ? true : len >= size;
                    int pos = rep ? localPos % len : localPos;
                    boolean matches = false;
                    if (sizeOk) {
                        matches = m.matches(pos, token);
                    }

                    if (matches) {
                        if (!rep && len == size) {
                            finalized.computeIfAbsent(size, c -> new ArrayList<>()).add(m.getName());
                            max = Math.max(max, size);
                        } else if (rep && size % len == 0) {
                            finalized.computeIfAbsent(size, c -> new ArrayList<>()).add(m.getName());
                            max = Math.max(max, size);
                        }

                    } else {
                        iterator.remove();
                    }

                }
                doMore = !toCheck.isEmpty();

            }

            if (finalized.isEmpty()) {
                max = Math.min(1, liveList.size());
                ArrayList<T> tokens = new ArrayList<>(max);

                for (int i = 0; i < max; i++) {
                    tokens.add(liveList.get(i));
                }
                for (int i = liveList.size() - 1; i >= max; i--) {
                    refillable.returnItem(liveList.get(i));
                }
                return Optional.of(new PosMatched<>(tokens));
            }

            List<I> maxMatched = finalized.get(max);

            ArrayList<T> tokens = new ArrayList<>(max);

            for (int i = 0; i < max; i++) {
                tokens.add(liveList.get(i));
            }
            for (int i = liveList.size() - 1; i >= max; i--) {
                refillable.returnItem(liveList.get(i));
            }
            return Optional.of(new PosMatched<>(maxMatched, tokens));

        }
        return Optional.empty();

    }

    public static <T, P extends PosMatch<T, P>> List<PosMatched<T, P>> tryMatchAll(Iterator<T> items, Collection<? extends P> matchersCol) throws MatchException {

        List<P> matchers = matchersCol.stream()
                .filter(p -> p.getLength() > 0)
                .sorted(cmpMatchers).collect(Collectors.toList());

        RefillableBuffer<T> refillable = new RefillableBuffer<>(items);
        ArrayList<PosMatched<T, P>> matched = new ArrayList<>();
        Optional<PosMatched<T, P>> findBestMatch = findBestMatch(refillable, matchers);

        while (findBestMatch.isPresent()) {
            matched.add(findBestMatch.get());
            findBestMatch = findBestMatch(refillable, matchers);
        }
        return matched;

    }
    protected List<P> matchers;
    protected RefillableBuffer<T> refillable;

    public SimplePosMatcherCombinator(Iterator<T> items, Collection<? extends P> matchers) {
        Objects.requireNonNull(matchers);
        this.refillable = new RefillableBuffer<>(items);

        this.matchers = matchers.stream()
                .filter(p -> p.getLength() > 0)
                .sorted(cmpMatchers)
                .collect(Collectors.toList());
    }

    public static <T, I> Iterator<PosMatched<T, I>> flatLift(Iterator<PosMatched<T, I>> iterator, Collection<? extends PosMatch<PosMatched<T, I>, I>> matchers) {
        Iterator<PosMatched<PosMatched<T, I>, I>> iterator1 = lift(iterator, matchers);
        return new Iterator<PosMatched<T, I>>() {
            @Override
            public boolean hasNext() {
                return iterator1.hasNext();
            }

            @Override
            public PosMatched<T, I> next() {
                PosMatched<PosMatched<T, I>, I> next = iterator1.next();
                if (next.matchedBy.isEmpty()) {
                    return next.getItem(0);
                }
                List<T> items = next.items.stream().flatMap(m -> m.items.stream()).collect(Collectors.toList());
                return new PosMatched<>(next.matchedBy, items);
            }
        };

    }
    
    public static <T, I> Iterator<PosMatched<PosMatched<T, I>,I>> lift(Iterator<PosMatched<T, I>> iterator, Collection<? extends PosMatch<PosMatched<T, I>, I>> matchers) {
        return new SimplePosMatcherCombinator<>(iterator, matchers).toSimplifiedIterator().iterator();
    }

    @Override
    protected Optional<List<PosMatched<T, I>>> produceNextList(Optional<List<PosMatched<T, I>>> currentList) throws Exception {

        Optional<PosMatched<T, I>> find = findBestMatch(refillable, matchers);
        return find.map(m -> {
            return Arrays.asList(m);
        });
    }
}
