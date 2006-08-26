package org.ctor.dev.llrps2.session;

public class NoSuchRpsCommandException extends RpsSessionException {
    private final static long serialVersionUID = 1;

    NoSuchRpsCommandException(RpsRole role, String command) {
        super(String.format("no such command for %s: %s", role, command));
    }
}
