package org.ctor.dev.llrps2;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public abstract class AbstractAgentTest extends TestCase {
    public AbstractAgentTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        return new TestSuite(AbstractAgentTest.class);
    }

    protected AssertCoordinator coordinator = null;

    @Override
    public void setUp() throws IOException {
        //
    }

    @Override
    public void tearDown() throws Exception {
        //
    }

    public void testSingleScenario() throws Exception {
        final int id1 = coordinator.connect();
        coordinator.sendHello(id1);
        coordinator.sendInitiate(id1);
        coordinator.receiveInitiate(id1);
        for (int round = 0; round < 5; ++round) {
            coordinator.sendRoundReady(id1);
            coordinator.receiveRoundReady(id1);
            for (int idx = 0; idx < 100; ++idx) {
                coordinator.sendCall(id1);
                coordinator.receiveMove(id1);
                coordinator.sendResultUpdate(id1);
            }
            coordinator.sendMatch(id1);
        }
        coordinator.sendClose(id1);
        coordinator.close(id1);
    }

    public void testMultipleScenario() throws Exception {
        final int id1 = coordinator.connect();
        coordinator.sendHello(id1);
        final int id2 = coordinator.connect();
        coordinator.sendInitiate(id1);
        coordinator.sendHello(id2);
        coordinator.sendInitiate(id2);
        final int id3 = coordinator.connect();
        coordinator.close(id3);
        coordinator.receiveInitiate(id2);
        coordinator.receiveInitiate(id1);
        for (int round = 0; round < 2; ++round) {
            coordinator.sendRoundReady(id2);
            coordinator.sendRoundReady(id1);
            coordinator.receiveRoundReady(id1);
            coordinator.receiveRoundReady(id2);
            for (int idx = 0; idx < 5; ++idx) {
                coordinator.sendCall(id1);
                coordinator.sendCall(id2);
                coordinator.receiveMove(id2);
                coordinator.sendResultUpdate(id2);
                coordinator.receiveMove(id1);
                coordinator.sendResultUpdate(id1);
            }
            coordinator.sendMatch(id2);
            coordinator.sendMatch(id1);
        }
        final int id4 = coordinator.connect();
        coordinator.close(id4);
        coordinator.sendClose(id1);
        coordinator.close(id1);
        coordinator.sendClose(id2);
        coordinator.close(id2);
    }

    public void testConnectClose() throws Exception {
        final int id1 = coordinator.connect();
        coordinator.close(id1);
        final int id2 = coordinator.connect();
        coordinator.close(id2);
        final int id3 = coordinator.connect();
        coordinator.close(id3);
    }

    public void testMultipleConnect() throws Exception {
        final int id1 = coordinator.connect();
        final int id2 = coordinator.connect();
        final int id3 = coordinator.connect();
        final int id4 = coordinator.connect();
        final int id5 = coordinator.connect();
        coordinator.close(id3);
        coordinator.close(id2);
        coordinator.close(id5);
        coordinator.close(id1);
        coordinator.close(id4);
    }

    public void test1() throws Exception {
        final int id1 = coordinator.connect();
        coordinator.close(id1);
    }

    public void test2() throws Exception {
        final int id1 = coordinator.connect();
        coordinator.close(id1);
        final int id2 = coordinator.connect();
        coordinator.close(id2);
    }

}
