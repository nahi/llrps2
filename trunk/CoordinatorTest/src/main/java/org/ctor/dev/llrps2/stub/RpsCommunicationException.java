package org.ctor.dev.llrps2.stub;

public class RpsCommunicationException extends RuntimeException {
    private final static long serialVersionUID = 1;

    RpsCommunicationException(String msg) {
        super(msg);
    }

    RpsCommunicationException(String msg, Throwable t) {
        super(msg, t);
    }

    RpsCommunicationException(Throwable t) {
        super(t);
    }
}
