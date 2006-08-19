package org.ctor.dev.llrps2.mediator;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;

import org.apache.commons.lang.Validate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.IllegalRpsMessageException;
import org.ctor.dev.llrps2.session.Rps;
import org.ctor.dev.llrps2.session.RpsCommand;
import org.ctor.dev.llrps2.session.RpsCommandParameter;
import org.ctor.dev.llrps2.session.RpsMessage;
import org.ctor.dev.llrps2.session.RpsRole;
import org.ctor.dev.llrps2.session.RpsSessionException;
import org.ctor.dev.llrps2.session.RpsState;
import org.ctor.dev.llrps2.session.RpsStateTransition;
import org.ctor.dev.llrps2.session.SessionStub;

public class SessionHandler {
    private static final Log LOG = LogFactory.getLog(SessionHandler.class);

    private final String coordinatorName;

    private RoundHandler roundHandler = null;

    private final String sessionId;

    private String roundId = null;

    private String iteration = null;

    private String ruleId = null;

    private String agentName = null;

    private String capacity = null;

    protected final RpsStateTransition state;

    private final SessionStub stub;

    static SessionHandler create(String coordinatorName, SocketChannel channel,
            String sessionId) {
        Validate.notNull(coordinatorName);
        Validate.notNull(channel);
        Validate.notNull(sessionId);
        return new SessionHandler(coordinatorName, channel, sessionId);
    }

    private SessionHandler(String coordinatorName, SocketChannel channel,
            String sessionId) {
        this.coordinatorName = coordinatorName;
        this.sessionId = sessionId;
        this.state = new RpsStateTransition(String.format("<COORDINATOR:%s>",
                this.coordinatorName));
        this.stub = new SessionStub(channel, RpsRole.AGENT);
    }

    public void handle() throws RpsSessionException, ClosedByInterruptException {
        LOG.info("handling the session");
        try {
            final long retrieveSize = stub.read();
            if (retrieveSize > 0) {
                RpsMessage message = null;
                while ((message = stub.receiveMessage()) != null) {
                    handleMessage(message);
                }
                stub.flushWrite();
            }
        } catch (ClosedByInterruptException cbie) {
            throw cbie;
        } catch (IOException ioe) {
            LOG.warn(ioe.getMessage(), ioe);
            throw new RpsSessionException(ioe);
        }
    }

    private void handleMessage(RpsMessage message) throws RpsSessionException {
        switch (message.getCommand()) {
        case A_HELLO:
            receiveHello(message);
            sendInitiate();
            break;
        case A_INITIATE:
            receiveInitiate(message);
            break;
        case A_READY:
            receiveRoundReady(message);
            break;
        case A_MOVE:
            receiveMove(message);
            break;
        default:
            throw new IllegalArgumentException("unknown command: "
                    + message.getCommand());
        }
    }

    void connect() throws RpsSessionException {
        state.transition(RpsState.ESTABLISHED);
    }

    void sendHello() throws RpsSessionException {
        LOG.info("sending HELLO");
        stub.sendMessage(RpsCommand.C_HELLO);
        flushWrite();
        state.transition(RpsState.C_HELLO);
    }

    void receiveHello(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving HELLO");
        state.transition(RpsState.A_HELLO);
    }

    void sendInitiate() throws RpsSessionException {
        LOG.info("sending INITIATE");
        stub.sendMessage(RpsCommand.C_INITIATE, sessionId);
        flushWrite();
        state.transition(RpsState.C_INITIATION);
    }

    void receiveInitiate(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving INITIATE");
        checkSessionId(message);
        agentName = message.getParameter(RpsCommandParameter.AgentName);
        capacity = message.getParameter(RpsCommandParameter.Capacity);
        state.transition(RpsState.INITIATED);
    }

    void sendRoundReady(String newRoundId, String newIteration, String newRuleId)
            throws RpsSessionException {
        LOG.info("sending READY");
        Validate.notNull(newRoundId);
        Validate.notNull(newIteration);
        Validate.notNull(newRuleId);
        this.roundId = newRoundId;
        this.iteration = newIteration;
        this.ruleId = newRuleId;
        stub.sendMessage(RpsCommand.C_READY, sessionId, newRoundId,
                newIteration, newRuleId);
        flushWrite();
        state.transition(RpsState.C_ROUND_READY);
    }

    void receiveRoundReady(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving READY");
        if (getRoundHandler() == null) {
            throw new IllegalStateException("roundHandler not set");
        }
        checkSessionId(message);
        checkRoundId(message);
        state.transition(RpsState.ROUND_READY);
        getRoundHandler().notifyGameReady(this);
    }

    void sendCall() throws RpsSessionException {
        LOG.info("sending CALL");
        stub.sendMessage(RpsCommand.C_CALL, sessionId, roundId);
        flushWrite();
        state.transition(RpsState.CALL);
    }

    void receiveMove(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving MOVE");
        if (getRoundHandler() == null) {
            throw new IllegalStateException("roundHandler not set");
        }
        state.transition(RpsState.MOVE);
        checkSessionId(message);
        checkRoundId(message);
        final Rps move = Rps.parse(message
                .getParameter(RpsCommandParameter.Move));
        getRoundHandler().notifyMove(this, move);
    }

    void sendResultUpdate(Rps previousOppositeMove) throws RpsSessionException {
        LOG.info("sending RESULT");
        stub.sendMessage(RpsCommand.C_RESULT, sessionId, roundId,
                previousOppositeMove.getRepresentation());
        flushWrite();
        state.transition(RpsState.RESULT_UPDATED);
    }

    void sendMatch() throws RpsSessionException {
        LOG.info("sending MATCH");
        stub.sendMessage(RpsCommand.C_MATCH, sessionId, roundId);
        flushWrite();
        state.transition(RpsState.MATCH);
    }

    void sendClose() throws RpsSessionException {
        LOG.info("sending CLOSE");
        stub.sendMessage(RpsCommand.C_CLOSE, sessionId);
        flushWrite();
        state.transition(RpsState.C_CLOSE);
    }

    void close() throws IOException {
        stub.close();
    }

    /* call this to read channel when channel can be read */
    long read() throws IOException {
        return stub.read();
    }

    boolean hasCachedMessage() {
        return stub.hasCachedMessage();
    }

    private void checkSessionId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkSessionId(sessionId);
    }

    private void checkRoundId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkRoundId(roundId);
    }

    private void flushWrite() throws RpsSessionException {
        try {
            stub.flushWrite();
        } catch (IOException ioe) {
            LOG.warn(ioe.getMessage(), ioe);
            throw new RpsSessionException(ioe);
        }
    }

    public void setRoundHandler(RoundHandler roundManager) {
        this.roundHandler = roundManager;
    }

    public RoundHandler getRoundHandler() {
        return roundHandler;
    }

    // XXX RpsState: MATCH had to be merged with INITIATED...  too late.
    public boolean isReadyForRoundStart() {
        return state.getState() == RpsState.INITIATED
                || state.getState() == RpsState.MATCH;
    }

    public SocketChannel getChannel() {
        return stub.getChannel();
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
