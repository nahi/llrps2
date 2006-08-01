package org.ctor.dev.llrps2.session;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionStub {
    private static final Log LOG = LogFactory.getLog(SessionStub.class);

    private static final int READ_BUFFER_SIZE = 8024;

    private static final String LINE_TERMINATER = "\r\n";

    private static final Charset CHARSET = Charset.forName("US-ASCII");

    private final SocketChannel channel;

    private final RpsRole oppositeRole;

    private StringBuilder readBuffer = new StringBuilder();

    private StringBuilder writeBuffer = new StringBuilder();

    public SessionStub(SocketChannel channel, RpsRole oppositeRole) {
        this.channel = channel;
        this.oppositeRole = oppositeRole;
    }

    public void sendMessage(RpsCommand command, String... rest) {
        if (ArrayUtils.contains(rest, null)) {
            throw new IllegalArgumentException("command contains null");
        }
        final RpsMessage message = RpsMessage.create(command, rest);
        writeMessage(message.dump());
    }

    public RpsMessage receiveMessage() throws RpsSessionException {
        int pos = readBuffer.indexOf(LINE_TERMINATER);
        if (pos == -1) {
            return null;
        }
        final String line = readBuffer.substring(0, pos);
        readBuffer.delete(0, pos + LINE_TERMINATER.length());
        return RpsMessage.parse(oppositeRole, line);
    }

    public void checkNoExtraMessage() {
        if (readBuffer.length() != 0) {
            throw new RpsCommunicationException("bulk messaging is not allowed");
        }
    }

    public long read() throws IOException {
        try {
            final ByteBuffer buffer = ByteBuffer.allocate(READ_BUFFER_SIZE);
            final long readSize = channel.read(buffer);
            if (readSize == -1) {
                LOG.info("reached end of stream");
                close();
            }
            else {
                buffer.flip();
                readBuffer.append(CHARSET.decode(buffer));
            }
            return readSize;
        }
        catch (IOException ioe) {
            LOG.info(ioe.getMessage(), ioe);
            throw ioe;
        }
    }

    public void flush() throws IOException {
        channel.write(CHARSET.encode(writeBuffer.toString()));
        writeBuffer.setLength(0);
    }
    
    public void close() throws IOException {
        channel.close();
    }

    private void writeMessage(String message) {
        writeLine(message + LINE_TERMINATER);
    }

    private void writeLine(String line) {
        writeBuffer.append(line);
    }
}
