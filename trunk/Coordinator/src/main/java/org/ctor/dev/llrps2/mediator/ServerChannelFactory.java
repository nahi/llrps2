package org.ctor.dev.llrps2.mediator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServerChannelFactory implements Runnable {
    private static final Log LOG = LogFactory
            .getLog(ServerChannelFactory.class);

    private int listenPort = 12346;

    private final Map<String, List<SocketChannel>> sessionPool = new HashMap<String, List<SocketChannel>>();

    private Thread serverThread = null;

    // synchronized is for clean shutdown
    public SocketChannel create(EnrolledAgent agent) {
        LOG.info("creating channel for " + agent.getAgent());
        if (serverThread == null) {
            startServerThread();
        }
        final List<SocketChannel> pool = sessionPool.get(agent.getAgent()
                .getIpAddress());
        if (pool == null || pool.size() == 0) {
            return null;
        }
        return pool.remove(0);
    }

    private void startServerThread() {
        LOG.info("starting server thread");
        serverThread = new Thread(this);
        serverThread.start();
    }

    public void run() {
        LOG.info("initializing ServerChannelFactory...");
        try {
            final Selector selector = Selector.open();
            final ServerSocketChannel serverChannel = ServerSocketChannel
                    .open();
            serverChannel.socket().bind(new InetSocketAddress(listenPort));
            serverChannel.configureBlocking(false);
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            LOG.info("ServerChannelFactory server start");
            while (true) {
                try {
                    selectAndProcess(selector, serverChannel);
                } catch (ClosedByInterruptException cbie) {
                    LOG.info(cbie.getMessage());
                    LOG.debug(cbie.getMessage(), cbie);
                    break;
                } catch (IOException ioe) {
                    LOG.warn(ioe.getMessage(), ioe);
                }
            }
            serverChannel.close();
            closeSessionPool();
            selector.close();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.info("ServerChannelFactory server stop");
    }

    private synchronized void closeSessionPool() {
        for (String ipAddress : sessionPool.keySet()) {
            final List<SocketChannel> channels = sessionPool.get(ipAddress);
            for (SocketChannel channel : channels) {
                try {
                    channel.close();
                } catch (IOException ioe) {
                    LOG.debug(ioe.getMessage(), ioe);
                }
            }
            LOG.info(String.format("closed %d sessions for %s",
                    channels.size(), ipAddress));
            channels.clear();
        }
        sessionPool.clear();
    }

    private synchronized void selectAndProcess(Selector selector,
            ServerSocketChannel serverChannel) throws IOException {
        selector.select();
        for (final SelectionKey key : selector.selectedKeys()) {
            if (key.isAcceptable()) {
                final SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    final String ipAddress = channel.socket().getInetAddress()
                            .getHostAddress();
                    List<SocketChannel> pool = sessionPool.get(ipAddress);
                    if (pool == null) {
                        pool = new ArrayList<SocketChannel>();
                        sessionPool.put(ipAddress, pool);
                    }
                    pool.add(channel);
                    LOG.info(String.format(
                            "accepted a new session for %s (%d sessions)",
                            ipAddress, pool.size()));
                }
            } else {
                throw new IllegalStateException();
            }
        }
    }

    public void setListenPort(int listenPort) {
        this.listenPort = listenPort;
    }

    public int getListenPort() {
        return listenPort;
    }
}
