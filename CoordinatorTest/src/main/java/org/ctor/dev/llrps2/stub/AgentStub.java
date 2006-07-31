package org.ctor.dev.llrps2.stub;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.RpsCommand;
import org.ctor.dev.llrps2.session.RpsMessage;
import org.ctor.dev.llrps2.session.RpsRole;
import org.ctor.dev.llrps2.session.RpsSessionException;

public class AgentStub {
    private static final Log LOG = LogFactory.getLog(AgentStub.class);

    private static final int READ_BUFFER_SIZE = 8024;

    private static final String LINE_TERMINATER = "\r\n";

    private static final Charset CHARSET = Charset.forName("US-ASCII");

    private final SocketChannel channel;

    private StringBuilder readBuffer = new StringBuilder();

    private StringBuilder writeBuffer = new StringBuilder();

    public AgentStub(SocketChannel channel) {
        this.channel = channel;
    }

    public void sendCommand(RpsCommand command, String... rest) {
        if (rest.length > 0) {
            writeMessage(String.format("%s %s", command.getCommand(),
                    StringUtils.join(rest, ' ')));
        }
        else {
            writeMessage(command.getCommand());
        }
    }

    public RpsMessage readMessage() throws RpsSessionException {
        final int pos = readBuffer.indexOf(LINE_TERMINATER);
        if (pos == -1) {
            return null;
        }
        final String line = readBuffer.substring(0, pos);
        readBuffer.delete(0, pos + LINE_TERMINATER.length());
        return RpsMessage.parse(RpsRole.COORDINATOR, line);
    }

    public long retrieve() throws IOException {
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
            LOG.info(ioe);
            close();
            throw ioe;
        }
    }

    public void flush() throws IOException {
        channel.write(CHARSET.encode(writeBuffer.toString()));
        writeBuffer.setLength(0);
    }

    private void writeMessage(String message) {
        writeLine(message + LINE_TERMINATER);
    }

    private void writeLine(String line) {
        writeBuffer.append(line);
    }

    public void close() throws IOException {
        channel.close();
    }
}
