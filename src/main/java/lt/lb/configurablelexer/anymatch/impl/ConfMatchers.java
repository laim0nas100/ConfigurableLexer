package lt.lb.configurablelexer.anymatch.impl;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import lt.lb.configurablelexer.anymatch.PosMatch;
import lt.lb.configurablelexer.anymatch.PosMatched;
import lt.lb.configurablelexer.anymatch.impl.ConfMatchers.PM;
import lt.lb.configurablelexer.anymatch.impl.ConfMatchers.PPM;
import lt.lb.configurablelexer.token.ConfToken;
import org.apache.commons.lang3.StringUtils;

/**
 * Matchers implementation with small type footprint
 *
 * @author laim0nas100
 */
public class ConfMatchers extends Matchers<ConfToken, String, PM, PPM, ConfMatchers> {

    protected AtomicLong atomicLong = new AtomicLong(0L);
    protected boolean nameSet = false;

    public static interface PM extends PosMatchDelegated<ConfToken, String> {

    }

    public static interface PPM extends PosMatchDelegated<PosMatched<ConfToken, String>, String> {

    }

    @Override
    public ConfMatchers makeNew(String name) {
        ConfMatchers makeNew = super.makeNew(name);
        makeNew.nameSet = true;
        return makeNew;
    }

    public PM exact(String value) {
        return isWhen(c -> StringUtils.equals(c.getValue(), value));
    }

    public PM ignoringCase(String value) {
        return isWhen(c -> StringUtils.equalsIgnoreCase(c.getValue(), value));
    }

    public PM whenString(Predicate<String> pred) {
        Objects.requireNonNull(pred, "Predicate must not be null");
        return isWhen(c -> pred.test(c.getValue()));
    }

    @Override
    protected PM simpleType(PosMatch<ConfToken, String> posMatched) {
        return () -> posMatched;
    }

    @Override
    protected PPM liftedType(PosMatch<PosMatched<ConfToken, String>, String> posMatchedLift) {
        return () -> posMatchedLift;
    }

    protected String autoname() {
        return atomicLong.incrementAndGet() + "-autoname";
    }

    @Override
    protected <K extends BasePosMatch<PosMatched<ConfToken, String>, String>> PPM decorateLifted(K k) {
        if (!nameSet) {
            this.name = autoname();
        }
        return super.decorateLifted(k);
    }

    @Override
    protected <K extends BasePosMatch<ConfToken, String>> PM decorate(K k) {
        if (!nameSet) {
            this.name = autoname();
        }
        return super.decorate(k);
    }

    @Override
    protected ConfMatchers create() {
        return new ConfMatchers();
    }

    @Override
    protected ConfMatchers me() {
        return this;
    }

}
