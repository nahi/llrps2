package org.ctor.dev.llrps2.stub;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.RpsCommand;
import org.ctor.dev.llrps2.session.RpsMessage;
import org.ctor.dev.llrps2.session.RpsRole;
import org.ctor.dev.llrps2.session.RpsSessionException;

public abstract class CoordinatorStub {
    private static final Log LOG = LogFactory.getLog(CoordinatorStub.class);

    private static final String LINE_TERMINATER = "\r\n";

    protected static final Charset CHARSET = Charset.forName("US-ASCII");

    private StringBuilder readBuffer = new StringBuilder();

    abstract public void connect() throws RpsSessionException, IOException;

    abstract public void close();

    public void sendCommand(RpsCommand command, String... rest) {
        if (ArrayUtils.contains(rest, null)) {
            throw new IllegalArgumentException("command contains null");
        }
        try {
            if (rest.length > 0) {
                writeMessage(String.format("%s %s", command.getCommand(),
                        StringUtils.join(rest, ' ')));
            }
            else {
                writeMessage(command.getCommand());
            }
        }
        catch (IOException ioe) {
            LOG.warn(ioe);
            throw new RpsCommunicationException(ioe);
        }
    }

    private void writeMessage(String message) throws IOException {
        writeLine(message + LINE_TERMINATER);
    }

    abstract protected void writeLine(String line) throws IOException;

    public RpsMessage readMessage() throws RpsSessionException {
        try {
            CharBuffer buf = null;
            do {
                buf = retrieve();
            } while (buf.length() == 0);
            readBuffer.append(buf);
        }
        catch (IOException ioe) {
            LOG.error(ioe);
            throw new RpsCommunicationException(ioe);
        }
        final int pos = readBuffer.indexOf(LINE_TERMINATER);
        if (pos == -1) {
            return null;
        }
        final String message = readBuffer.substring(0, pos);
        readBuffer.delete(0, pos + LINE_TERMINATER.length());
        return RpsMessage.parse(RpsRole.AGENT, message);
    }

    abstract protected CharBuffer retrieve() throws IOException;

    public void checkReadBuffer() {
        if (readBuffer.length() != 0) {
            throw new RpsCommunicationException("bulk messaging is not allowed");
        }
    }
}
