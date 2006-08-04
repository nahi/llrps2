package org.ctor.dev.llrps2;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestClientAgent extends AbstractAgentTest {
    private AssertServerCoordinator coordinator = null;

    public TestClientAgent(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestClientAgent.class);
    }

    @Override
    public void setUp() throws IOException {
        super.setUp();
        coordinator = createCoordinator();
    }

    @Override
    protected AssertCoordinator coordinator() {
        return coordinator;
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        coordinator.terminate();
    }

    private AssertServerCoordinator createCoordinator() {
        final AssertServerCoordinator proxy = new AssertServerCoordinator(
                listenPort);
        proxy.start();
        return proxy;
    }
}
