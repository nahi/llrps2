package org.ctor.dev.llrps2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.RpsSessionException;

final public class SampleAgent extends Thread {
    private static final Log LOG = LogFactory.getLog(SampleAgent.class);

    private static final int DEFAULT_LISTEN_PORT = 2006;

    private final HashMap<SocketChannel, RpsAgentSessionHandler> handlerMap = new HashMap<SocketChannel, RpsAgentSessionHandler>();

    private final int listenPort;

    public SampleAgent() {
        this(DEFAULT_LISTEN_PORT);
    }

    public SampleAgent(int listenPort) {
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
                    break;
                }
                catch (IOException ioe) {
                    LOG.info(ioe);
                }
                catch (RpsSessionException irste) {
                    LOG.warn(irste);
                }

            }
            serverChannel.close();
            selector.close();
        }
        catch (IOException ioe) {
            LOG.error(ioe);
            throw new RuntimeException(ioe);
        }
        LOG.info("agent stop");
    }

    public void terminate() throws IOException {
        interrupt();
    }

    private void selectAndProcess(Selector selector,
            ServerSocketChannel serverChannel) throws IOException,
            RpsSessionException {
        selector.select();
        for (final SelectionKey key : selector.selectedKeys()) {
            if (key.isAcceptable()) {
                final SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    LOG.info("accepted");
                }
            }
            else if (key.isReadable()) {
                final SocketChannel channel = (SocketChannel) key.channel();
                final RpsAgentSessionHandler handler = getHandler(channel);
                handler.handle();
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

    public static void main(final String[] arg) {
        (new SampleAgent()).start();
    }
}
