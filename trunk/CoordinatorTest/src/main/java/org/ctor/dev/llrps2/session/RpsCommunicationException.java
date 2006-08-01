package org.ctor.dev.llrps2.session;

public class RpsCommunicationException extends RuntimeException {
    private final static long serialVersionUID = 1;

    public RpsCommunicationException(String msg) {
        super(msg);
    }

    public RpsCommunicationException(String msg, Throwable t) {
        super(msg, t);
    }

    public RpsCommunicationException(Throwable t) {
        super(t);
    }
}
