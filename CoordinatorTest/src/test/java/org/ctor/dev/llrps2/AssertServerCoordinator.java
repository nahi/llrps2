package org.ctor.dev.llrps2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.AssertRpsCoordinatorSessionHandler;
import org.ctor.dev.llrps2.session.RpsSessionException;

public class AssertServerCoordinator extends AssertCoordinator implements
        Runnable {
    private static final Log LOG = LogFactory
            .getLog(AssertServerCoordinator.class);

    private static final int DEFAULT_LISTEN_PORT = 2006;

    private final BlockingQueue<SocketChannel> sessionPool = new ArrayBlockingQueue<SocketChannel>(
            5);

    private int handlerCounter = 0;

    private final int listenPort;

    private Thread thread = null;

    public AssertServerCoordinator() {
        this(DEFAULT_LISTEN_PORT);
    }

    public AssertServerCoordinator(int listenPort) {
        this.listenPort = listenPort;
    }

    public void start() {
        thread = new Thread(this);
        thread.start();
    }

    public boolean isAlive() {
        if (thread == null) {
            return false;
        }
        return thread.isAlive();
    }

    public void run() {
        LOG.info("initializing coordinator...");
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
                    LOG.warn(cbie.getMessage(), cbie);
                    break;
                }
                catch (IOException ioe) {
                    LOG.warn(ioe.getMessage(), ioe);
                }
            }
            serverChannel.close();
            selector.close();
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        LOG.info("agent stop");
    }

    public void terminate() {
        if (thread != null) {
            LOG.info("terminate");
            thread.interrupt();
        }
    }

    @Override
    public int connect() throws RpsSessionException {
        try {
            final SocketChannel channel = sessionPool.take();
            LOG.info("assign a channel from session pool");
            if (channel == null) {
                throw new IllegalStateException();
            }
            final AssertRpsCoordinatorSessionHandler handler = new AssertRpsCoordinatorSessionHandler(
                    channel);
            final int id = handlerCounter++;
            handlerMap.put(id, handler);
            channelMap.put(id, channel);
            handler.connect();
            receiveHello(id);
            return id;
        }
        catch (InterruptedException ie) {
            LOG.warn(ie.getMessage(), ie);
            throw new RpsSessionException(ie);
        }
    }

    private void selectAndProcess(Selector selector,
            ServerSocketChannel serverChannel) throws IOException {
        selector.select();
        for (final SelectionKey key : selector.selectedKeys()) {
            if (key.isAcceptable()) {
                final SocketChannel channel = serverChannel.accept();
                if (channel != null) {
                    LOG.info("accepted");
                    sessionPool.add(channel);
                }
            }
            else {
                throw new IllegalStateException();
            }
        }
    }

    @Override
    public void sendHello(int id) throws RpsSessionException {
        // do nothing
        // server type coordinator accepts request and gets A_HELLO.
    }
}
