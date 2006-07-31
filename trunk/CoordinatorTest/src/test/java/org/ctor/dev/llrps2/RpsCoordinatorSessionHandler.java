package org.ctor.dev.llrps2;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.IllegalRpsMessageException;
import org.ctor.dev.llrps2.session.Rps;
import org.ctor.dev.llrps2.session.RpsCommand;
import org.ctor.dev.llrps2.session.RpsCommandParameter;
import org.ctor.dev.llrps2.session.RpsMessage;
import org.ctor.dev.llrps2.session.RpsSessionException;
import org.ctor.dev.llrps2.session.RpsState;
import org.ctor.dev.llrps2.session.RpsStateTransition;
import org.ctor.dev.llrps2.stub.CoordinatorStub;

import static junit.framework.Assert.*;

public class RpsCoordinatorSessionHandler {
    private static final Log LOG = LogFactory
            .getLog(RpsCoordinatorSessionHandler.class);

    protected final RpsStateTransition state = new RpsStateTransition(
            RpsCoordinatorSessionHandler.class.getName());

    private final CoordinatorStub stub;

    private String sessionId = "SESSION-ID_123.456";

    private String roundId = "01234567890123456789012345678901234567890123456789012345678901234567890123456_80";

    private String iteration = "10";

    private String ruleId = "123";

    private Rps previousOppositeMove = Rps.Rock;

    private String agentName = null;

    private String capacity = null;

    public RpsCoordinatorSessionHandler(CoordinatorStub stub) {
        this.stub = stub;
    }

    public void connect() throws RpsSessionException, IOException {
        stub.connect();
        state.transition(RpsState.ESTABLISHED);
    }

    public void sendHello() throws RpsSessionException {
        LOG.info("sending HELLO");
        stub.sendCommand(RpsCommand.C_HELLO);
        state.transition(RpsState.C_HELLO);
    }

    public void receiveHello() throws RpsSessionException {
        LOG.info("receiving HELLO");
        final RpsMessage message = stub.readMessage();
        assertEquals(RpsCommand.A_HELLO, message.getCommand());
        stub.checkReadBuffer();
        state.transition(RpsState.INITIATED);
    }

    public void sendInitiate() throws RpsSessionException {
        LOG.info("sending INITIATE");
        stub.sendCommand(RpsCommand.C_INITIATE, sessionId);
        state.transition(RpsState.C_INITIATION);
    }

    public void receiveInitiate() throws RpsSessionException {
        LOG.info("receiving INITIATE");
        final RpsMessage message = stub.readMessage();
        assertEquals(RpsCommand.A_INITIATE, message.getCommand());
        stub.checkReadBuffer();
        checkSessionId(message);
        agentName = message.getParameter(RpsCommandParameter.AgentName);
        capacity = message.getParameter(RpsCommandParameter.Capacity);
        state.transition(RpsState.INITIATED);
    }

    public void sendRoundReady() throws RpsSessionException {
        LOG.info("sending READY");
        stub.sendCommand(RpsCommand.C_READY, sessionId, roundId, iteration,
                ruleId);
        state.transition(RpsState.C_ROUND_READY);
    }

    public void receiveRoundReady() throws RpsSessionException {
        LOG.info("receiving READY");
        final RpsMessage message = stub.readMessage();
        assertEquals(RpsCommand.A_READY, message.getCommand());
        stub.checkReadBuffer();
        checkSessionId(message);
        checkRoundId(message);
        state.transition(RpsState.ROUND_READY);
    }

    public void sendCall() throws RpsSessionException {
        LOG.info("sending CALL");
        stub.sendCommand(RpsCommand.C_CALL, sessionId, roundId);
        state.transition(RpsState.CALL);
    }

    public Rps receiveMove() throws RpsSessionException {
        LOG.info("receiving MOVE");
        state.transition(RpsState.MOVE);
        final RpsMessage message = stub.readMessage();
        assertEquals(RpsCommand.A_MOVE, message.getCommand());
        stub.checkReadBuffer();
        checkSessionId(message);
        checkRoundId(message);
        // XXX
        return Rps.Rock;
    }

    public void sendResultUpdate() throws RpsSessionException {
        LOG.info("sending RESULT");
        stub.sendCommand(RpsCommand.C_RESULT, sessionId, roundId,
                previousOppositeMove.getRepresentation());
        state.transition(RpsState.RESULT_UPDATED);
    }

    public void sendMatch() throws RpsSessionException {
        LOG.info("sending MATCH");
        stub.sendCommand(RpsCommand.C_MATCH, sessionId, roundId);
        state.transition(RpsState.MATCH);
    }

    public void sendClose() throws RpsSessionException {
        LOG.info("sending CLOSE");
        stub.sendCommand(RpsCommand.C_CLOSE, sessionId);
        state.transition(RpsState.C_CLOSE);
    }

    public void close() throws RpsSessionException {
        stub.close();
        state.transition(RpsState.END);
    }

    private void checkSessionId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkSessionId(sessionId);
    }

    private void checkRoundId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkRoundId(roundId);
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setRoundId(String roundId) {
        this.roundId = roundId;
    }

    public String getRoundId() {
        return roundId;
    }

    public void setIteration(String iteration) {
        this.iteration = iteration;
    }

    public String getIteration() {
        return iteration;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setPreviousOppositeMove(Rps previousOppositeMove) {
        this.previousOppositeMove = previousOppositeMove;
    }

    public Rps getPreviousOppositeMove() {
        return previousOppositeMove;
    }

    public String getAgentName() {
        return agentName;
    }

    public String getCapacity() {
        return capacity;
    }
}
