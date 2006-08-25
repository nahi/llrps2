package org.ctor.dev.llrps2.mediator;

import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.IllegalRpsMessageException;
import org.ctor.dev.llrps2.session.Rps;
import org.ctor.dev.llrps2.session.RpsMessage;
import org.ctor.dev.llrps2.session.RpsSessionException;
import org.ctor.dev.llrps2.session.RpsState;
import org.ctor.dev.llrps2.session.RpsStateTransition;

public abstract class SessionHandler {
    private static final Log LOG = LogFactory.getLog(SessionHandler.class);

    private RoundHandler roundHandler = null;

    protected final String sessionId;

    protected String roundId = null;

    protected String iteration = null;

    protected String ruleId = null;

    protected String agentName = null;

    protected String capacity = null;

    protected boolean inRecoveryMode = false;

    protected final RpsStateTransition state;

    protected SessionHandler(String sessionId) {
        this.sessionId = sessionId;
        this.state = new RpsStateTransition(String.format("<COORDINATOR:%s>",
                this.sessionId));
    }

    abstract void connect() throws RpsSessionException;

    abstract void sendHello() throws RpsSessionException;

    abstract void receiveHello(RpsMessage message) throws RpsSessionException;

    abstract void sendInitiate() throws RpsSessionException;

    abstract void receiveInitiate(RpsMessage message)
            throws RpsSessionException;

    abstract void sendRoundReady(String newRoundId, String newIteration,
            String newRuleId) throws RpsSessionException;

    abstract void receiveRoundReady(RpsMessage message)
            throws RpsSessionException;

    abstract void sendCall() throws RpsSessionException;

    abstract void receiveMove(RpsMessage message) throws RpsSessionException;

    abstract void sendResultUpdate(Rps previousOppositeMove)
            throws RpsSessionException;

    abstract void sendMatch() throws RpsSessionException;

    abstract void sendClose() throws RpsSessionException;

    abstract void close();

    public void recover() throws RpsSessionException {
        LOG.info(String.format("%s - %s: recovery from %s", sessionId,
                getAgentName(), state.getState()));
        switch (state.getState()) {
        case START:
        case ESTABLISHED:
        case A_HELLO:
        case C_HELLO:
        case C_INITIATION:
        case C_CLOSE:
        case END:
            throw new IllegalStateException("should not happen: "
                    + state.getState());
        case INITIATED:
        case C_ROUND_READY:
        case ROUND_READY:
        case MATCH:
            // nothing to do
            break;
        case CALL:
            inRecoveryMode = true;
            break;
        case MOVE:
            sendResultUpdate(Rps.NotAMove);
            sendMatch();
            break;
        case RESULT_UPDATED:
            sendMatch();
            break;
        default:
            throw new IllegalArgumentException("unknown state "
                    + state.getState());
        }
    }

    protected void checkSessionId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkSessionId(sessionId);
    }

    protected void checkRoundId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkRoundId(roundId);
    }

    public void setRoundHandler(RoundHandler roundManager) {
        this.roundHandler = roundManager;
    }

    public RoundHandler getRoundHandler() {
        return roundHandler;
    }

    // XXX RpsState: MATCH had to be merged with INITIATED... too late.
    public boolean isReadyForRoundStart() {
        return state.getState() == RpsState.INITIATED
                || state.getState() == RpsState.MATCH;
    }

    abstract boolean isConnected();

    abstract SocketChannel getChannel();
    
    public RpsState getState() {
        return state.getState();
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRoundId() {
        return roundId;
    }

    public String getIteration() {
        return iteration;
    }

    public String getRuleId() {
        return ruleId;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getCapacity() {
        return capacity;
    }
}
