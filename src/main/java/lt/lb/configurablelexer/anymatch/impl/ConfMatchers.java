package lt.lb.configurablelexer.anymatch.impl;

import lt.lb.configurablelexer.anymatch.PosMatch;
import lt.lb.configurablelexer.anymatch.PosMatched;
import lt.lb.configurablelexer.anymatch.impl.ConfMatchers.PM;
import lt.lb.configurablelexer.anymatch.impl.ConfMatchers.PPM;
import lt.lb.configurablelexer.token.ConfToken;

/**
 * Matchers implementation with small type footprint
 * @author laim0nas100
 */
public class ConfMatchers extends Matchers<ConfToken, String, PM, PPM, ConfMatchers> {

    public static interface PM extends PosMatchDelegated<ConfToken, String> {

    }

    public static interface PPM extends PosMatchDelegated<PosMatched<ConfToken, String>, String> {

    }

    @Override
    protected PM simpleType(PosMatch<ConfToken, String> posMatched) {
        return () -> posMatched;
    }

    @Override
    protected PPM liftedType(PosMatch<PosMatched<ConfToken, String>, String> posMatchedLift) {
        return () -> posMatchedLift;
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
