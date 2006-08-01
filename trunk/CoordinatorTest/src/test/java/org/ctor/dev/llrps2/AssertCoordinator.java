package org.ctor.dev.llrps2;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.AssertRpsCoordinatorSessionHandler;
import org.ctor.dev.llrps2.session.Rps;
import org.ctor.dev.llrps2.session.RpsSessionException;
import org.ctor.dev.llrps2.session.RpsCommunicationException;

public abstract class AssertCoordinator {
    private static final Log LOG = LogFactory.getLog(AssertCoordinator.class);

    private long selectTimeout = 2000;

    protected final Map<Integer, AssertRpsCoordinatorSessionHandler> handlerMap = new HashMap<Integer, AssertRpsCoordinatorSessionHandler>();

    protected final Map<Integer, SocketChannel> channelMap = new HashMap<Integer, SocketChannel>();

    abstract public int connect() throws RpsSessionException;

    public void close(int id) {
        try {
            getHandler(id).close();
        }
        catch (IOException ioe) {
            LOG.warn(ioe.getMessage(), ioe);
        }
    }

    abstract public void sendHello(int id) throws RpsSessionException;

    public void receiveHello(int id) throws RpsSessionException {
        read(id);
        getHandler(id).receiveHello();
    }

    public void sendInitiate(int id) throws RpsSessionException {
        getHandler(id).sendInitiate();
    }

    public void receiveInitiate(int id) throws RpsSessionException {
        read(id);
        getHandler(id).receiveInitiate();
    }

    public void sendRoundReady(int id) throws RpsSessionException {
        getHandler(id).sendRoundReady();
    }

    public void receiveRoundReady(int id) throws RpsSessionException {
        read(id);
        getHandler(id).receiveRoundReady();
    }

    public void sendCall(int id) throws RpsSessionException {
        getHandler(id).sendCall();
    }

    public Rps receiveMove(int id) throws RpsSessionException {
        read(id);
        return getHandler(id).receiveMove();
    }

    public void sendResultUpdate(int id) throws RpsSessionException {
        getHandler(id).sendResultUpdate();
    }

    public void sendMatch(int id) throws RpsSessionException {
        getHandler(id).sendMatch();
    }

    public void sendClose(int id) throws RpsSessionException {
        getHandler(id).sendClose();
    }

    public void setSessionId(int id, String sessionId) {
        getHandler(id).setSessionId(sessionId);
    }

    public String getSessionId(int id) {
        return getHandler(id).getSessionId();
    }

    public void setRoundId(int id, String roundId) {
        getHandler(id).setRoundId(roundId);
    }

    public String getRoundId(int id) {
        return getHandler(id).getRoundId();
    }

    public void setIteration(int id, String iteration) {
        getHandler(id).setIteration(iteration);
    }

    public String getIteration(int id) {
        return getHandler(id).getIteration();
    }

    public void setRuleId(int id, String ruleId) {
        getHandler(id).setRuleId(ruleId);
    }

    public String getRuleId(int id) {
        return getHandler(id).getRuleId();
    }

    public void setPreviousOppositeMove(int id, Rps previousOppositeMove) {
        getHandler(id).setPreviousOppositeMove(previousOppositeMove);
    }

    public Rps getPreviousOppositeMove(int id) {
        return getHandler(id).getPreviousOppositeMove();
    }

    public String getAgentName(int id) {
        return getHandler(id).getAgentName();
    }

    public String getCapacity(int id) {
        return getHandler(id).getCapacity();
    }

    private void read(int id) throws RpsSessionException {
        try {
            final Selector selector = Selector.open();
            final SocketChannel channel = getChannel(id);
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_READ);
            selector.select(selectTimeout);
            if (selector.selectedKeys().size() == 1) {
                if (getHandler(id).read() > 0) {
                    return;
                }
                close(id);
                throw new IllegalStateException();
            }
            close(id);
            throw new RpsCommunicationException(String.format(
                    "receiving timeout: %d [msec]", selectTimeout));
        }
        catch (IOException ioe) {
            LOG.warn(ioe.getMessage(), ioe);
            close(id);
            throw new RpsSessionException(ioe);
        }
    }

    protected AssertRpsCoordinatorSessionHandler getHandler(int id) {
        if (!handlerMap.containsKey(id)) {
            throw new IllegalArgumentException("illegal ID");
        }
        return handlerMap.get(id);
    }

    private SocketChannel getChannel(int id) {
        if (!channelMap.containsKey(id)) {
            throw new IllegalArgumentException("illegal ID");
        }
        return channelMap.get(id);
    }
}
