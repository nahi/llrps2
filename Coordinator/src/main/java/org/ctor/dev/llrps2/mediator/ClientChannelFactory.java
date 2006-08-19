package org.ctor.dev.llrps2.mediator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ClientChannelFactory {
    private static final Log LOG = LogFactory
            .getLog(ClientChannelFactory.class);

    public SocketChannel create(EnrolledAgent agent) {
        LOG.info("connecting to " + agent.getAgent() + "...");
        try {
            final String ipAddress = agent.getAgent().getIpAddress();
            final int port = agent.getAgent().getPort();
            final InetSocketAddress address = new InetSocketAddress(InetAddress
                    .getByName(ipAddress), port);
            final SocketChannel channel = SocketChannel.open();
            channel.connect(address);
            LOG.info("connected");
            return channel;
        } catch (IOException ioe) {
            LOG.info(ioe.getMessage(), ioe);
        }
        return null;
    }
}
