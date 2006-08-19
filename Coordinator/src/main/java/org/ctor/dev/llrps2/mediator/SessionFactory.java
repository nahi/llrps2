package org.ctor.dev.llrps2.mediator;

import java.nio.channels.SocketChannel;

import org.ctor.dev.llrps2.session.RpsSessionException;

public class SessionFactory {

    private ServerChannelFactory serverChannelFactory = null;

    private ClientChannelFactory clientChannelFactory = null;

    public SessionHandler create(EnrolledAgent agent, String coordinatorName,
            String sessionId) throws RpsSessionException {
        final SocketChannel channel = createChannel(agent);
        if (channel == null) {
            return null;
        }
        final SessionHandler handler = SessionHandler.create(coordinatorName,
                channel, sessionId);
        handler.connect();
        if (!agent.getAgent().isActive()) {
            handler.sendHello();
            handler.sendInitiate();
        }
        return handler;
    }

    private SocketChannel createChannel(EnrolledAgent agent) {
        if (agent.getAgent().isActive()) {
            return serverChannelFactory.create(agent);
        } else {
            return clientChannelFactory.create(agent);
        }
    }

    public void setServerChannelFactory(ServerChannelFactory serverFactory) {
        this.serverChannelFactory = serverFactory;
    }

    public ServerChannelFactory getServerChannelFactory() {
        return serverChannelFactory;
    }

    public void setClientChannelFactory(ClientChannelFactory clientFactory) {
        this.clientChannelFactory = clientFactory;
    }

    public ClientChannelFactory getClientChannelFactory() {
        return clientChannelFactory;
    }
}
