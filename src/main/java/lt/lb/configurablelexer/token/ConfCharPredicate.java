package lt.lb.configurablelexer.token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;

/**
 *
 * @author laim0nas100
 */
public class ConfCharPredicate implements IntPredicate {

    protected Set<Integer> allowedChars;
    protected Set<Integer> disallowedChars;
    protected List<IntPredicate> allowed;
    protected List<IntPredicate> disallowed;
    protected boolean defaultCondition = true;

    public ConfCharPredicate setDefault(boolean defaultCondition) {
        this.defaultCondition = defaultCondition;
        return this;
    }

    public ConfCharPredicate allowChars(Collection<Integer> ac) {
        if (allowedChars == null) {
            allowedChars = new HashSet<>();
        }
        allowedChars.addAll(ac);
        return this;
    }

    public ConfCharPredicate allowChars(int... chars) {
        if (allowedChars == null) {
            allowedChars = new HashSet<>();
        }
        for (int c : chars) {
            allowedChars.add(c);
        }
        return this;
    }

    public ConfCharPredicate disallowChars(Collection<Integer> ac) {
        if (disallowedChars == null) {
            disallowedChars = new HashSet<>();
        }
        disallowedChars.addAll(ac);
        return this;
    }

    public ConfCharPredicate disallowChars(int... chars) {
        if (disallowedChars == null) {
            disallowedChars = new HashSet<>();
        }
        for (int c : chars) {
            disallowedChars.add(c);
        }
        return this;
    }

    public ConfCharPredicate allowWhen(IntPredicate pred) {
        Objects.requireNonNull(pred, "Predicate is null");
        if (allowed == null) {
            allowed = new ArrayList<>();
        }
        allowed.add(pred);
        return this;
    }

    public ConfCharPredicate disallowWhen(IntPredicate pred) {
        Objects.requireNonNull(pred, "Predicate is null");
        if (disallowed == null) {
            disallowed = new ArrayList<>();
        }
        disallowed.add(pred);
        return this;
    }

    @Override
    public boolean test(int c) {
        if (allowed != null) {
            for (IntPredicate pred : allowed) {
                if (pred.test(c)) {
                    return true;
                }
            }
        }
        if (disallowed != null) {
            for (IntPredicate pred : disallowed) {
                if (pred.test(c)) {
                    return false;
                }
            }
        }
        if (allowedChars != null) {
            if (allowedChars.contains(c)) {
                return true;
            }
        }
        if (disallowedChars != null) {
            return !disallowedChars.contains(c);
        } else {
            return defaultCondition; // same as disalowed is empty
        }
    }

    public static IntPredicate ofChars(int... chars) {
        return c -> {
            for (int i : chars) {
                if (c == i) {
                    return true;
                }
            }
            return false;
        };
    }

}
