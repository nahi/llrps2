package org.ctor.dev.llrps2;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ServerAgentTest extends AbstractAgentTest {
    public ServerAgentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ServerAgentTest.class);
    }

    private static final int LISTEN_PORT = 12345;

    private SampleAgent agent = null;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        agent = new SampleAgent(LISTEN_PORT);
        coordinator = createCoordinator();
        agent.start();
    }

    private AssertClientCoordinator createCoordinator() throws IOException {
        return new AssertClientCoordinator(null, LISTEN_PORT);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        agent.terminate();
        while (agent.isAlive()) {
            Thread.sleep(100);
        }
        agent = null;
    }
}
