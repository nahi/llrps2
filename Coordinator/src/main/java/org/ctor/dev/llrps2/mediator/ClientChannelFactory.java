package org.ctor.dev.llrps2.mediator;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.commons.lang.StringUtils;
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
                    .getByAddress(parseIpAddress(ipAddress)), port);
            final SocketChannel channel = SocketChannel.open();
            channel.connect(address);
            LOG.info("connected");
            return channel;
        } catch (IOException ioe) {
            LOG.info(ioe.getMessage(), ioe);
        }
        return null;
    }

    // XXX handles IPv4 address only
    private byte[] parseIpAddress(String ipAddress) {
        final String[] particles = StringUtils.split(ipAddress, '.');
        final byte[] bytes = new byte[particles.length];
        for (int idx = 0; idx < particles.length; ++idx) {
            bytes[idx] = (byte)Integer.parseInt(particles[idx]);
        }
        return bytes;
    }
}
