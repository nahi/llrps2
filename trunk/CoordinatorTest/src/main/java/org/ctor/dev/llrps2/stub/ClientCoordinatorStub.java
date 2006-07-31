package org.ctor.dev.llrps2.stub;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.RpsSessionException;

public class ClientCoordinatorStub extends CoordinatorStub {
    private static final Log LOG = LogFactory
            .getLog(ClientCoordinatorStub.class);

    private static final int READ_BUFFER_SIZE = 8024;

    private final String host;

    private final int port;

    private long selectTimeout = 1000;

    private Selector selector = null;

    private SocketChannel channel = null;

    public ClientCoordinatorStub(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() throws RpsSessionException, IOException {
        LOG.info("connectting");
        final InetSocketAddress address = new InetSocketAddress(InetAddress
                .getByName(host), port);
        selector = Selector.open();
        channel = SocketChannel.open();
        channel.connect(address);
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_READ);
    }

    @Override
    public void close() {
        try {
            channel.close();
        }
        catch (IOException ioe) {
            LOG.warn(ioe);
        }
    }

    @Override
    protected void writeLine(String message) throws IOException {
        channel.write(CHARSET.encode(message));
    }

    @Override
    protected CharBuffer retrieve() throws IOException {
        if (!channel.isConnected()) {
            throw new RpsCommunicationException("already disconnected");
        }
        selector.select(selectTimeout);
        for (final SelectionKey key : selector.selectedKeys()) {
            if (key.isReadable()) {
                final ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
                final long readSize = channel.read(buffer);
                if (readSize == -1) {
                    close();
                    throw new RpsCommunicationException("reached end of stream");
                }
                else {
                    buffer.flip();
                    return CHARSET.decode(buffer);
                }
            }
        }
        close();
        throw new RpsCommunicationException(String.format(
                "receiving timeout: %d [msec]", selectTimeout));
    }

    public void setSelectTimeout(long selectTimeout) {
        this.selectTimeout = selectTimeout;
    }

    public long getSelectTimeout() {
        return selectTimeout;
    }
}
