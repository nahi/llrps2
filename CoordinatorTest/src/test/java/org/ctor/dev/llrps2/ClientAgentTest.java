package org.ctor.dev.llrps2;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class ClientAgentTest extends AbstractAgentTest {
    public ClientAgentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ClientAgentTest.class);
    }

    private static final int LISTEN_PORT = 12345;

    private SampleClientAgent agent = null;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        agent = new SampleClientAgent(null, LISTEN_PORT);
        coordinator = createCoordinator();
        agent.start();
    }

    private AssertServerCoordinator createCoordinator() {
        final AssertServerCoordinator proxy = new AssertServerCoordinator(
                LISTEN_PORT);
        proxy.start();
        return proxy;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        agent.terminate();
        while (agent.isAlive()) {
            Thread.sleep(100);
        }
        final AssertServerCoordinator c = (AssertServerCoordinator) coordinator;
        c.terminate();
        while (c.isAlive()) {
            Thread.sleep(100);
        }
    }
}
