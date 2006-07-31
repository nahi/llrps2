package org.ctor.dev.llrps2;

import org.ctor.dev.llrps2.stub.ClientCoordinatorStub;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ServerAgentTest extends TestCase {
    public ServerAgentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(ServerAgentTest.class);
    }

    private static final int LISTEN_PORT = 12345;

    private SampleAgent agent = null;

    private RpsCoordinatorSessionHandler c1 = null;

    @Override
    public void setUp() {
        agent = new SampleAgent(LISTEN_PORT);
        agent.start();
        c1 = createCoordinatorSessionHandler();
    }

    private RpsCoordinatorSessionHandler createCoordinatorSessionHandler() {
        return new RpsCoordinatorSessionHandler(new ClientCoordinatorStub(null,
                LISTEN_PORT));
    }

    @Override
    public void tearDown() throws Exception {
        agent.terminate();
        // XXX should wait a thread termination
        while (agent.isAlive()) {
            Thread.sleep(100);
        }
        agent = null;
    }

    public void testSingleScenario() throws Exception {
        c1.connect();
        c1.sendHello();
        c1.sendInitiate();
        c1.receiveInitiate();
        for (int round = 0; round < 5; ++round) {
            c1.sendRoundReady();
            c1.receiveRoundReady();
            for (int idx = 0; idx < 100; ++idx) {
                c1.sendCall();
                c1.receiveMove();
                c1.sendResultUpdate();
            }
            c1.sendMatch();
        }
        c1.sendClose();
        c1.close();
    }

    public void testMultipleScenario() throws Exception {
        final RpsCoordinatorSessionHandler c2 = createCoordinatorSessionHandler();
        final RpsCoordinatorSessionHandler c3 = createCoordinatorSessionHandler();
        final RpsCoordinatorSessionHandler c4 = createCoordinatorSessionHandler();
        c1.connect();
        c1.sendHello();
        c2.connect();
        c1.sendInitiate();
        c2.sendHello();
        c2.sendInitiate();
        c3.connect();
        c3.close();
        c2.receiveInitiate();
        c1.receiveInitiate();
        for (int round = 0; round < 2; ++round) {
            c2.sendRoundReady();
            c1.sendRoundReady();
            c1.receiveRoundReady();
            c2.receiveRoundReady();
            for (int idx = 0; idx < 5; ++idx) {
                c1.sendCall();
                c2.sendCall();
                c2.receiveMove();
                c2.sendResultUpdate();
                c1.receiveMove();
                c1.sendResultUpdate();
            }
            c2.sendMatch();
            c1.sendMatch();
        }
        c4.connect();
        c4.close();
        c1.sendClose();
        c1.close();
        c2.sendClose();
        c2.close();
    }

    public void testConnectClose() throws Exception {
        c1.connect();
        c1.close();
        final RpsCoordinatorSessionHandler c2 = createCoordinatorSessionHandler();
        c2.connect();
        c2.close();
        final RpsCoordinatorSessionHandler c3 = createCoordinatorSessionHandler();
        c3.connect();
        c3.close();
    }

    public void testMultipleConnect() throws Exception {
        final RpsCoordinatorSessionHandler c2 = createCoordinatorSessionHandler();
        final RpsCoordinatorSessionHandler c3 = createCoordinatorSessionHandler();
        final RpsCoordinatorSessionHandler c4 = createCoordinatorSessionHandler();
        final RpsCoordinatorSessionHandler c5 = createCoordinatorSessionHandler();
        c1.connect();
        c2.connect();
        c3.connect();
        c4.connect();
        c5.connect();
        c1.close();
        c2.close();
        c3.close();
        c4.close();
        c5.close();
    }

    public void test1() throws Exception {
        c1.connect();
        c1.close();
    }

    public void test2() throws Exception {
        c1.connect();
        c1.close();
    }

}
