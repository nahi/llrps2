package org.ctor.dev.llrps2;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SampleServerAgentTest extends TestServerAgent {
    public SampleServerAgentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SampleServerAgentTest.class);
    }

    private SampleServerAgent agent = null;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        agent = new SampleServerAgent(listenPort);
        agent.start();
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
