package org.ctor.dev.llrps2.session;

public class IllegalRpsStateTransitionException extends RpsSessionException {
    private final static long serialVersionUID = 1;

    IllegalRpsStateTransitionException(RpsState from, RpsState to) {
        super(String.format("%s -> %s", from, to));
    }
}
