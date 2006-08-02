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

    private int sessionCounter = 0;

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
                try {
                    while (selector.keys().size() < 5) {
                        LOG.info("opening the new session");
                        final SocketChannel channel = SocketChannel.open();
                        channel.connect(address);
                        final RpsAgentSessionHandler handler = getHandler(channel);
                        handler.sendHello();
                        channel.configureBlocking(false);
                        channel.register(selector, SelectionKey.OP_READ);
                    }
                    if (selector.keys().size() > 0) {
                        selectAndProcess(selector);
                    }
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
        }
        catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        LOG.info("agent stop");
    }

    public void terminate() {
        LOG.info("terminating the thread");
        interrupt();
    }

    private void selectAndProcess(Selector selector) throws IOException,
            RpsSessionException {
        selector.select();
        LOG.info(String.format("selected %d keys", selector.selectedKeys()
                .size()));
        for (SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable()) {
                final SocketChannel channel = (SocketChannel) key.channel();
                final RpsAgentSessionHandler handler = getHandler(channel);
                try {
                    handler.handle();
                }
                catch (ClosedByInterruptException cbie) {
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
            handlerMap.put(channel, new RpsAgentSessionHandler(String
                    .valueOf(sessionCounter++), channel));
        }
        return handlerMap.get(channel);
    }

    public static void main(final String[] arg) throws UnknownHostException {
        (new SampleClientAgent(null)).start();
    }
}
