package org.ctor.dev.llrps2;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TestServerAgent extends AbstractAgentTest {
    public TestServerAgent(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(TestServerAgent.class);
    }

    private AssertClientCoordinator coordinator = null;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        coordinator = createCoordinator();
    }

    private AssertClientCoordinator createCoordinator() throws IOException {
        return new AssertClientCoordinator(null, listenPort);
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        coordinator.closeAll();
    }

    @Override
    protected AssertCoordinator coordinator() {
        return coordinator;
    }
}
