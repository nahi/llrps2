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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.RpsSessionException;

final public class SampleClientAgent extends Thread {
    private static final int SESSION_POOL_SIZE = 20;

    private static final Log LOG = LogFactory.getLog(SampleClientAgent.class);

    private static final int DEFAULT_LISTEN_PORT = 12346;

    private final Set<SocketChannel> sessionPool = new HashSet<SocketChannel>();

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
                while (sessionPool.size() < SESSION_POOL_SIZE) {
                    final SocketChannel channel = createChannel();
                    channel.configureBlocking(false);
                    channel.register(selector, SelectionKey.OP_READ);
                    sessionPool.add(channel);
                }
                try {
                    LOG.info("sessions: " + sessionPool.size());
                    selectAndProcess(selector);
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
            closeSessionPool();
            selector.close();
        }
        catch (Exception e) {
            LOG.warn(e.getMessage(), e);
        }
        LOG.info("agent stop");
    }

    private void closeSessionPool() {
        for (SocketChannel channel : sessionPool.toArray(new SocketChannel[0])) {
            try {
                channel.close();
            }
            catch (IOException ioe) {
                LOG.debug(ioe.getMessage(), ioe);
            }
        }
    }

    private SocketChannel createChannel() throws IOException,
            RpsSessionException {
        LOG.info("creating the new session");
        final SocketChannel channel = SocketChannel.open();
        channel.connect(address);
        final RpsAgentSessionHandler handler = getHandler(channel);
        handler.sendHello();
        return channel;
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
                    removeSession(channel);
                    handler.close();
                    throw cbie;
                }
                catch (Exception e) {
                    removeSession(channel);
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

    private void removeSession(SocketChannel channel) {
        sessionPool.remove(channel);
        handlerMap.remove(channel);
    }

    private RpsAgentSessionHandler getHandler(SocketChannel channel) {
        if (handlerMap.get(channel) == null) {
            handlerMap.put(channel, new RpsAgentSessionHandler(String
                    .valueOf(sessionCounter++), channel));
        }
        return handlerMap.get(channel);
    }

    public static void main(final String[] arg) throws UnknownHostException {
        if (arg.length != 2) {
            throw new IllegalArgumentException("host and port required");
        }
        final String host = arg[0];
        final int port = Integer.parseInt(arg[1]);
        (new SampleClientAgent(host, port)).start();
    }
}
