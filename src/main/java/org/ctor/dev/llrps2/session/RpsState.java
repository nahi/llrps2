package org.ctor.dev.llrps2.session;

public enum RpsState {
    // start
    START,
    // TCP connection established
    ESTABLISHED,
    // coordinator sent HELLO if a coordinator opens TCP connection
    C_HELLO,
    // agent sent HELLO if an agent opens TCP connection
    A_HELLO,
    // coordinator sent session initiation message
    C_INITIATION,
    // agent respond initiation message
    INITIATED,
    // coordinator asked round-ready
    C_ROUND_READY,
    // agent accepted round-ready
    ROUND_READY,
    // coordinator call for move
    CALL,
    // agent sent its move
    MOVE,
    // coordinator sent the previous result
    RESULT_UPDATED,
    // coordinator calls match
    MATCH,
    // coordinator noticed the session closing.
    C_CLOSE,
    // end
    END;

    static {
        RpsStateTransition.define(START, ESTABLISHED, END);
        RpsStateTransition.define(ESTABLISHED, A_HELLO, C_HELLO, END);
        RpsStateTransition.define(C_HELLO, C_INITIATION, END);
        RpsStateTransition.define(A_HELLO, C_INITIATION, END);
        RpsStateTransition.define(C_INITIATION, INITIATED, END);
        RpsStateTransition.define(INITIATED, C_ROUND_READY, C_CLOSE, END);
        RpsStateTransition.define(C_ROUND_READY, ROUND_READY, END);
        RpsStateTransition.define(ROUND_READY, CALL, MATCH, END);
        RpsStateTransition.define(CALL, MOVE, END);
        RpsStateTransition.define(MOVE, RESULT_UPDATED, END);
        RpsStateTransition.define(RESULT_UPDATED, CALL, MATCH, END);
        RpsStateTransition.define(MATCH, C_ROUND_READY, C_CLOSE, END);
        RpsStateTransition.define(C_CLOSE, END);
    }
}
