package org.ctor.dev.llrps2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.AssertRpsCoordinatorSessionHandler;
import org.ctor.dev.llrps2.session.RpsSessionException;

public class AssertClientCoordinator extends AssertCoordinator {
    private static final Log LOG = LogFactory
            .getLog(AssertClientCoordinator.class);

    private final InetSocketAddress address;

    private int handlerCounter = 0;

    public AssertClientCoordinator(String host, int port) throws IOException {
        this.address = new InetSocketAddress(InetAddress.getByName(host), port);
    }

    @Override
    public int connect() throws RpsSessionException {
        LOG.info("connecting");
        try {
            final SocketChannel channel = SocketChannel.open();
            channel.connect(address);
            final int id = handlerCounter++;
            final AssertRpsCoordinatorSessionHandler handler = new AssertRpsCoordinatorSessionHandler(
                    String.valueOf(id), channel);
            handlerMap.put(id, handler);
            channelMap.put(id, channel);
            handler.connect();
            return id;
        }
        catch (IOException ioe) {
            LOG.warn(ioe.getMessage(), ioe);
            throw new RpsSessionException(ioe);
        }
    }

    @Override
    public void sendHello(int id) throws RpsSessionException {
        getHandler(id).sendHello();
    }
}
