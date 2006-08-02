package org.ctor.dev.llrps2.session;

public class NoSuchRpsMoveException extends RpsSessionException {
    private final static long serialVersionUID = 1;

    NoSuchRpsMoveException(String move) {
        super("no such move: " + move);
    }
}
