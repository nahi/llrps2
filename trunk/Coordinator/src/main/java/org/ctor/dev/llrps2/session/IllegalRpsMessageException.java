package org.ctor.dev.llrps2.session;

public class IllegalRpsMessageException extends RpsSessionException {
    private final static long serialVersionUID = 1;

    public IllegalRpsMessageException(String line) {
        super(line);
    }

    public IllegalRpsMessageException(String line, Throwable t) {
        super(line, t);
    }

    public IllegalRpsMessageException(Throwable t) {
        super(t);
    }
}
