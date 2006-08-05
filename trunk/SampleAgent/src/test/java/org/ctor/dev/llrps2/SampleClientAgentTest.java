package org.ctor.dev.llrps2;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;

public class SampleClientAgentTest extends TestClientAgent {
    public SampleClientAgentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(SampleClientAgentTest.class);
    }

    private SampleClientAgent agent = null;

    @Override
    public void setUp() throws IOException {
        super.setUp();
        agent = new SampleClientAgent(null, listenPort);
        agent.start();
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        agent.terminate();
        while (agent.isAlive()) {
            Thread.sleep(100);
        }
    }
}
