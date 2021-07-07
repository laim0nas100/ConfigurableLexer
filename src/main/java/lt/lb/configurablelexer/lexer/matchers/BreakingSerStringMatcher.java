package lt.lb.configurablelexer.lexer.matchers;

/**
 *
 * @author laim0nas100
 */
public abstract class BreakingSerStringMatcher extends IdStringMatcher {

    protected boolean breaking;

    @Override
    public boolean canBeBreaking() {
        return isBreaking();
    }

    public boolean isBreaking() {
        return breaking;
    }

    public void setBreaking(boolean breaking) {
        this.breaking = breaking;
    }

    protected Match makeMatch() {
        return isBreaking() ? Match.fullMatchBreak() : Match.fullMatch();
    }

    protected Match makeMatch(int from, int to) {
        return isBreaking() ? Match.matchBreak(from, to) : Match.match(from, to);
    }

    @Override
    public String stringValues() {
        return "breaking=" + breaking;
    }

    @Override
    public String toString() {
        return descriptiveString();
    }

}
