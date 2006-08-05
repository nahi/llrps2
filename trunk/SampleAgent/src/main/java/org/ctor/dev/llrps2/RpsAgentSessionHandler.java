package org.ctor.dev.llrps2;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;

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

public class RpsAgentSessionHandler {
    private static final Log LOG = LogFactory
            .getLog(RpsAgentSessionHandler.class);

    private final RpsStateTransition state;

    private final String agentName;

    private final SessionStub stub;

    private String sessionId = null;

    private String roundId = null;

    private String capacity = "123";

    private String iteration = null;

    private String ruleId = null;

    private int gameCounter = 0;

    public RpsAgentSessionHandler(String agentName, SocketChannel channel) {
        this.agentName = agentName;
        this.state = new RpsStateTransition(String.format("<AGENT:%s>",
                agentName));
        this.stub = new SessionStub(channel, RpsRole.COORDINATOR);
        try {
            connect();
        }
        catch (RpsSessionException rse) {
            throw new IllegalStateException(rse);
        }
    }

    // for client type agent only
    public void sendHello() throws RpsSessionException {
        LOG.info("sending HELLO");
        stub.sendMessage(RpsCommand.C_HELLO);
        try {
            stub.flushWrite();
        }
        catch (IOException ioe) {
            throw new RpsSessionException(ioe);
        }
        state.transition(RpsState.A_HELLO);
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
        }
        catch (ClosedByInterruptException cbie) {
            throw cbie;
        }
        catch (IOException ioe) {
            LOG.warn(ioe.getMessage(), ioe);
            throw new RpsSessionException(ioe);
        }
    }

    public void close() throws IOException {
        LOG.info("closing the session");
        stub.close();
    }

    // XXX use commands if the spec is fixed >> gotoken
    private void handleMessage(RpsMessage message) throws IOException,
            RpsSessionException {
        switch (message.getCommand()) {
        case C_CALL:
            receiveCall(message);
            stub.checkNoCachedMessage();
            final Rps[] moves = Rps.getCandidates();
            final Rps move = moves[(++gameCounter % moves.length)];
            sendMove(move);
            break;
        case C_CLOSE:
            receiveClose(message);
            stub.checkNoCachedMessage();
            close();
            break;
        case C_HELLO:
            receiveHello(message);
            break;
        case C_INITIATE:
            receiveInitiate(message);
            stub.checkNoCachedMessage();
            sendInitiate();
            break;
        case C_MATCH:
            receiveMatch(message);
            break;
        case C_READY:
            receiveRoundReady(message);
            stub.checkNoCachedMessage();
            sendRoundReady();
            break;
        case C_RESULT:
            receiveResultUpdate(message);
            break;
        default:
            throw new IllegalArgumentException("unknown command: "
                    + message.getCommand());
        }
    }

    private void connect() throws RpsSessionException {
        LOG.info("connecting");
        state.transition(RpsState.ESTABLISHED);
    }

    private void receiveHello(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving HELLO");
        state.transition(RpsState.C_HELLO);
    }

    private void receiveInitiate(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving INITIATE");
        sessionId = message.getParameter(RpsCommandParameter.SessionId);
        checkSessionId(message);
        LOG.info("initiate session id: " + sessionId);
        state.transition(RpsState.C_INITIATION);
    }

    private void sendInitiate() throws RpsSessionException {
        LOG.info("sending INITIATE");
        stub.sendMessage(RpsCommand.A_INITIATE, sessionId, agentName, capacity);
        LOG.info("send agent name: " + agentName + ", capacity: " + capacity);
        state.transition(RpsState.INITIATED);
    }

    private void receiveRoundReady(RpsMessage message)
            throws RpsSessionException {
        LOG.info("receiving READY");
        checkSessionId(message);
        roundId = message.getParameter(RpsCommandParameter.RoundId);
        iteration = message.getParameter(RpsCommandParameter.Iteration);
        ruleId = message.getParameter(RpsCommandParameter.RuleId);
        LOG.info("ready round id: " + roundId + ", iteration: " + iteration
                + ", rule id: " + ruleId);
        state.transition(RpsState.C_ROUND_READY);
    }

    private void sendRoundReady() throws RpsSessionException {
        LOG.info("sending READY");
        stub.sendMessage(RpsCommand.A_READY, sessionId, roundId);
        state.transition(RpsState.ROUND_READY);
    }

    private void receiveCall(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving CALL");
        checkSessionId(message);
        checkRoundId(message);
        state.transition(RpsState.CALL);
    }

    private void sendMove(Rps move) throws RpsSessionException {
        LOG.info("sending MOVE");
        stub.sendMessage(RpsCommand.A_MOVE, sessionId, roundId, move
                .getRepresentation());
        state.transition(RpsState.MOVE);
    }

    private void receiveResultUpdate(RpsMessage message)
            throws RpsSessionException {
        LOG.info("receiving RESULT");
        checkSessionId(message);
        checkRoundId(message);
        final String previousOppositeMove = message
                .getParameter(RpsCommandParameter.PreviousOppositeMove);
        LOG.info("result previous opposite move: " + previousOppositeMove);
        state.transition(RpsState.RESULT_UPDATED);
    }

    private void receiveMatch(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving MATCH");
        checkSessionId(message);
        checkRoundId(message);
        state.transition(RpsState.MATCH);
    }

    private void receiveClose(RpsMessage message) throws RpsSessionException {
        LOG.info("receiving CLOSE");
        checkSessionId(message);
        state.transition(RpsState.C_CLOSE);
    }

    private void checkSessionId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkSessionId(sessionId);
    }

    private void checkRoundId(RpsMessage message)
            throws IllegalRpsMessageException {
        message.checkRoundId(roundId);
    }
}
