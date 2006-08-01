package org.ctor.dev.llrps2;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.RpsSessionException;

final public class SampleClientAgent extends Thread {
    private static final Log LOG = LogFactory.getLog(SampleClientAgent.class);

    private static final int DEFAULT_LISTEN_PORT = 2006;

    private final Map<SocketChannel, RpsAgentSessionHandler> handlerMap = new HashMap<SocketChannel, RpsAgentSessionHandler>();

    private final InetSocketAddress address;

    public SampleClientAgent(String host) throws UnknownHostException {
        this(host, DEFAULT_LISTEN_PORT);
    }

    public SampleClientAgent(String host, int port) throws UnknownHostException {
        address = new InetSocketAddress(InetAddress.getByName(host), port);
        LOG.info("agent start");
    }

    @Override
    public void run() {
        LOG.info("initializing agent...");
        try {
            final Selector selector = Selector.open();
            while (true) {
                while (selector.keys().size() < 5) {
                    final SocketChannel channel = SocketChannel.open();
                    channel.connect(address);
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    final RpsAgentSessionHandler handler = getHandler(channel);
                    handler.sendHello();
                }
                while (selector.keys().size() > 0) {
                    selectAndProcess(selector);
                }
            }
        }
        catch(Exception e) {
            LOG.warn(e.getMessage(), e);
        }
    }

    public void terminate() {
        LOG.info("terminate");
        interrupt();
    }

    private void selectAndProcess(Selector selector)
            throws IOException, RpsSessionException {
        selector.select();
        for (final SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable()) {
                final SocketChannel channel = (SocketChannel)key.channel();
                final RpsAgentSessionHandler handler = getHandler(channel); 
                try {
                    handler.handle();
                    return;
                }
                catch(ClosedByInterruptException cbie) {
                    handler.close();
                    throw cbie;
                }
                catch (Exception e) {
                    handler.close();
                    LOG.warn(e.getMessage(), e);
                    throw new RpsSessionException(e);
                }
            }
            else {
                throw new IllegalStateException();
            }
        }
    }

    private RpsAgentSessionHandler getHandler(final SocketChannel channel) {
        if (handlerMap.get(channel) == null) {
            handlerMap.put(channel, new RpsAgentSessionHandler(channel));
        }
        return handlerMap.get(channel);
    }

    public static void main(final String[] arg) throws UnknownHostException {
        (new SampleClientAgent(null)).start();
    }
}
