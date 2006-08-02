package org.ctor.dev.llrps2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.RpsSessionException;

final public class SampleServerAgent extends Thread {
    private static final Log LOG = LogFactory.getLog(SampleServerAgent.class);

    private static final int DEFAULT_LISTEN_PORT = 2006;

    private final Map<SocketChannel, RpsAgentSessionHandler> handlerMap = new HashMap<SocketChannel, RpsAgentSessionHandler>();

    private final int listenPort;

    private int sessionCounter = 0;

    public SampleServerAgent() {
        this(DEFAULT_LISTEN_PORT);
    }

    public SampleServerAgent(int listenPort) {
        this.listenPort = listenPort;
    }

    @Override
    public void run() {
        LOG.info("initializing agent...");
        try {
            final Selector selector = Selector.open();
            final ServerSocketChannel serverChannel = ServerSocketChannel
                    .open();
            serverChannel.socket().bind(new InetSocketAddress(listenPort));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            LOG.info("agent start");
            while (true) {
                try {
                    selectAndProcess(selector, serverChannel);
                }
                catch (ClosedByInterruptException cbie) {
                    LOG.info(cbie.getMessage());
                    LOG.debug(cbie.getMessage(), cbie);
                    break;
                }
                catch (IOException ie) {
                    LOG.warn(ie.getMessage(), ie);
                }
                catch (RpsSessionException rse) {
                    LOG.warn(rse.getMessage(), rse);
                }
            }
            serverChannel.close();
            selector.close();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.info("agent stop");
    }

    public void terminate() {
        LOG.info("terminating the thread");
        interrupt();
    }

    private void selectAndProcess(Selector selector,
            ServerSocketChannel serverChannel) throws IOException,
            RpsSessionException {
        selector.select();
        LOG.info(String.format("selected %d keys", selector.selectedKeys()
                .size()));
        for (final SelectionKey key : selector.selectedKeys()) {
            if (key.isAcceptable()) {
                final SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    LOG.info("accepted the new session");
                }
            }
            else if (key.isReadable()) {
                final SocketChannel channel = (SocketChannel) key.channel();
                final RpsAgentSessionHandler handler = getHandler(channel);
                try {
                    handler.handle();
                }
                catch (RpsSessionException rse) {
                    handler.close();
                    throw rse;
                }
            }
            else {
                throw new IllegalStateException();
            }
        }
    }

    private RpsAgentSessionHandler getHandler(final SocketChannel channel) {
        if (handlerMap.get(channel) == null) {
            handlerMap.put(channel, new RpsAgentSessionHandler(String
                    .valueOf(sessionCounter++), channel));
        }
        return handlerMap.get(channel);
    }

    public static void main(final String[] arg) {
        (new SampleServerAgent()).start();
    }
}
