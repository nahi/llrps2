package org.ctor.dev.llrps2.mediator;

import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.session.RpsSessionException;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Mediator {
    private static final Log LOG = LogFactory.getLog(Mediator.class);

    private AgentEnrollmentManager agentEnrollmentManager = null;

    private RoundMediationManager roundMediationManager = null;

    private SessionFactory sessionFactory = null;

    private String coordinatorName = "LLRPS2";

    private int connectionScanInterleaveMsec = 2000;

    private int maxConnectionsForPassiveAgent = 5;

    private Selector selector = null;

    private int sessionCounter = 0;

    private Map<SocketChannel, SessionHandler> handlerMap = new HashMap<SocketChannel, SessionHandler>();

    public static void main(String[] args) throws IOException {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:org/ctor/dev/llrps2/mediator/applicationContext.xml");
        final Mediator mgr = (Mediator) ctx.getBean("mediator");
        mgr.start();
    }

    void start() throws IOException {
        LOG.info("mediator started");
        selector = Selector.open();
        while (true) {
            try {
                selectAndProcess();
            } catch (ClosedByInterruptException cbie) {
                LOG.info(cbie.getMessage());
                LOG.debug(cbie.getMessage(), cbie);
                break;
            } catch (IOException ie) {
                LOG.warn(ie.getMessage(), ie);
            } catch (RpsSessionException rse) {
                LOG.warn(rse.getMessage(), rse);
            }
        }
        LOG.info("mediator stopped");
    }

    void notifyAgentEnrollmentRequest() {
        scanConnection();
    }

    void notifyRoundMediationRequest() {
        scanRound();
    }

    private void selectAndProcess() throws IOException, RpsSessionException {
        final int keys = selector.select(connectionScanInterleaveMsec);
        if (keys == 0) {
            scanConnection();
            scanRound();
            return;
        }
        LOG.info(String.format("selected %d keys", keys));
        final Iterator<SelectionKey> ite = selector.selectedKeys().iterator();
        while (ite.hasNext()) {
            final SelectionKey key = ite.next();
            if (!key.isReadable()) {
                throw new IllegalStateException();
            }
            ite.remove();
            final SocketChannel channel = (SocketChannel) key.channel();
            final SessionHandler handler = getHandler(channel);
            try {
                handler.handle();
            } catch (RpsSessionException rse) {
                handler.close();
                removeHandler(channel);
                throw rse;
            }
        }
    }

    private void scanConnection() {
        if (agentEnrollmentManager.getAgents().size() == 0) {
            return;
        }
        boolean added = false;
        for (EnrolledAgent agent : agentEnrollmentManager.getAgents()) {
            final int connections = agent.connections();
            if (agent.getAgent().isActive()) {
                if (connections > 0) {
                    continue;
                }
            } else {
                if (connections >= maxConnectionsForPassiveAgent) {
                    continue;
                }
            }
            LOG.info("trying to create new connection for " + agent.getAgent());
            try {
                final SessionHandler handler = sessionFactory.create(agent,
                        coordinatorName, newSessionId());
                if (handler == null) {
                    LOG.info("cannot create");
                } else {
                    final SocketChannel channel = handler.getChannel();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    assignHandler(channel, handler);
                    agent.addSession(handler);
                    added = true;
                }
            } catch (RpsSessionException rse) {
                LOG.warn(rse.getMessage(), rse);
            } catch (IOException ioe) {
                LOG.warn(ioe.getMessage(), ioe);
            }
        }
        if (added) {
            agentEnrollmentManager.notifyConnectedAgents();
        }
    }

    private void scanRound() {
        if (roundMediationManager.getRounds().size() == 0) {
            return;
        }
        LOG.info("scanning rounds...");
        for (RoundMessage round : roundMediationManager.getRounds()) {
            if (!round.isAssigned()) {
                assignRound(round);
            }
        }
    }

    private void assignRound(RoundMessage round) {
        LOG.info("trying to assign a round: " + round);
        final EnrolledAgent leftAgent = agentEnrollmentManager.findAgent(round
                .getLeft());
        if (leftAgent == null) {
            throw new IllegalStateException("leftagent not enrolled: "
                    + round.getLeft());
        }
        final EnrolledAgent rightAgent = agentEnrollmentManager.findAgent(round
                .getRight());
        if (rightAgent == null) {
            throw new IllegalStateException("right agent not enrolled: "
                    + round.getRight());
        }
        final SessionHandler leftSession = pollSession(leftAgent);
        if (leftSession == null) {
            LOG.info("no valid connection found for left agent");
            return;
        }
        final SessionHandler rightSession = pollSession(rightAgent);
        if (rightSession == null) {
            LOG.info("no valid connection found for right agent");
            return;
        }
        if (!leftSession.isReadyForRoundStart()
                || !rightSession.isReadyForRoundStart()) {
            // push_back
            leftAgent.addSession(leftSession);
            rightAgent.addSession(rightSession);
            if (!leftSession.isReadyForRoundStart()) {
                LOG.info("leftSession not ready (state "
                        + leftSession.getState() + ")");
            }
            if (!rightSession.isReadyForRoundStart()) {
                LOG.info("rightSession not ready (state "
                        + rightSession.getState() + ")");
            }
            return;
        }
        try {
            RoundHandler.create(roundMediationManager, round, leftSession,
                    rightSession);
            round.setAssigned(true);
            LOG.info("assigned a new round");
        } catch (RpsSessionException rse) {
            LOG.warn("RoundHandler create failed");
        } finally {
            // push_back
            leftAgent.addSession(leftSession);
            rightAgent.addSession(rightSession);
        }
    }

    private SessionHandler pollSession(EnrolledAgent agent) {
        SessionHandler session = null;
        while ((session = agent.pollSession()) != null) {
            if (session.isConnected()) {
                return session;
            }
            LOG.info("fail: polled session was closed");
        }
        return null;
    }

    private String newSessionId() {
        sessionCounter += 1;
        return String.format("S_%d", sessionCounter);
    }
    
    private void assignHandler(SocketChannel channel, SessionHandler handler) {
        handlerMap.put(channel, handler);
    }

    private void removeHandler(SocketChannel channel) {
        handlerMap.remove(channel);
    }

    private SessionHandler getHandler(SocketChannel channel) {
        return handlerMap.get(channel);
    }

    public void setAgentEnrollmentManager(
            AgentEnrollmentManager agentEnrollmentManager) {
        this.agentEnrollmentManager = agentEnrollmentManager;
    }

    public AgentEnrollmentManager getAgentEnrollmentManager() {
        return agentEnrollmentManager;
    }

    public void setRoundMediationManager(
            RoundMediationManager roundMediationManager) {
        this.roundMediationManager = roundMediationManager;
    }

    public RoundMediationManager getRoundMediationManager() {
        return roundMediationManager;
    }

    public void setSessionFactory(SessionFactory channelManager) {
        this.sessionFactory = channelManager;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setCoordinatorName(String coordinatorName) {
        this.coordinatorName = coordinatorName;
    }

    public String getCoordinatorName() {
        return coordinatorName;
    }

    public void setConnectionScanInterleaveMsec(int connectionScanInterleaveMsec) {
        this.connectionScanInterleaveMsec = connectionScanInterleaveMsec;
    }

    public int getConnectionScanInterleaveMsec() {
        return connectionScanInterleaveMsec;
    }

    public void setMaxConnectionsForPassiveAgent(
            int maxConnectionsForPassiveAgent) {
        this.maxConnectionsForPassiveAgent = maxConnectionsForPassiveAgent;
    }

    public int getMaxConnectionsForPassiveAgent() {
        return maxConnectionsForPassiveAgent;
    }
}
