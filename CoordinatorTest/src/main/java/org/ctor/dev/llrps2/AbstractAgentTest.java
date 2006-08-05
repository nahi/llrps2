package org.ctor.dev.llrps2;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.session.Rps;

import junit.framework.TestCase;

public abstract class AbstractAgentTest extends TestCase {
    private static final Log LOG = LogFactory.getLog(AbstractAgentTest.class);

    private int scenarioRoundNumber = 5;

    private int scenarioGameNumber = 20;

    protected int listenPort = 12346;

    public AbstractAgentTest(String testName) {
        super(testName);
        final InputStream stream = getClass().getResourceAsStream(
                "/llrps2.properties");
        if (stream != null) {
            final Properties properties = new Properties();
            try {
                properties.load(stream);
            }
            catch (IOException ioe) {
                LOG.warn(ioe.getMessage(), ioe);
            }
            final String roundNumber = properties
                    .getProperty("llrps2.coordinator()test.scenarioroundnumber");
            if (roundNumber != null) {
                scenarioRoundNumber = Integer.parseInt(roundNumber);
            }
            final String gameNumber = properties
                    .getProperty("llrps2.coordinator()test.scenariogamenumber");
            if (gameNumber != null) {
                scenarioGameNumber = Integer.parseInt(gameNumber);
            }
            final String port = properties
                    .getProperty("llrps2.coordinator()test.port");
            if (port != null) {
                listenPort = Integer.parseInt(port);
            }
        }
    }

    abstract protected AssertCoordinator coordinator();

    @Override
    public void setUp() throws IOException {
        //
    }

    @Override
    public void tearDown() throws Exception {
        //
    }

    private void scenarioJustOneMove(Rps move) throws Exception {
        final int id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        for (int round = 0; round < scenarioRoundNumber; ++round) {
            coordinator().setRoundId(id, String.format("R_%d", round));
            coordinator().sendRoundReady(id);
            coordinator().receiveRoundReady(id);
            for (int idx = 0; idx < scenarioGameNumber; ++idx) {
                coordinator().sendCall(id);
                coordinator().receiveMove(id);
                coordinator().setPreviousOppositeMove(id, move);
                coordinator().sendResultUpdate(id);
            }
            coordinator().sendMatch(id);
        }
        coordinator().sendClose(id);
        coordinator().close(id);
    }

    public void testScenarioJustRock() throws Exception {
        scenarioJustOneMove(Rps.Rock);
    }

    public void testScenarioJustScissors() throws Exception {
        scenarioJustOneMove(Rps.Scissors);
    }

    public void testScenarioJustPaper() throws Exception {
        scenarioJustOneMove(Rps.Paper);
    }

    public void testScenarioRotate() throws Exception {
        final int id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        for (int round = 0; round < scenarioRoundNumber; ++round) {
            coordinator().setRoundId(id, String.format("R_%d", round));
            coordinator().sendRoundReady(id);
            coordinator().receiveRoundReady(id);
            for (int idx = 0; idx < scenarioGameNumber; ++idx) {
                coordinator().sendCall(id);
                coordinator().receiveMove(id);
                final Rps[] moves = Rps.getCandidates();
                coordinator().setPreviousOppositeMove(id,
                        moves[idx % moves.length]);
                coordinator().sendResultUpdate(id);
            }
            coordinator().sendMatch(id);
        }
        coordinator().sendClose(id);
        coordinator().close(id);
    }

    public void testScenarioCopy() throws Exception {
        final int id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        for (int round = 0; round < scenarioRoundNumber; ++round) {
            coordinator().setRoundId(id, String.format("R_%d", round));
            coordinator().sendRoundReady(id);
            coordinator().receiveRoundReady(id);
            Rps previous = Rps.NotAMove;
            for (int idx = 0; idx < scenarioGameNumber; ++idx) {
                coordinator().sendCall(id);
                coordinator().setPreviousOppositeMove(id, previous);
                previous = coordinator().receiveMove(id);
                coordinator().sendResultUpdate(id);
            }
            coordinator().sendMatch(id);
        }
        coordinator().sendClose(id);
        coordinator().close(id);
    }

    public void testScenarioCopy2() throws Exception {
        final int id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        for (int round = 0; round < scenarioRoundNumber; ++round) {
            coordinator().setRoundId(id, String.format("R_%d", round));
            coordinator().sendRoundReady(id);
            coordinator().receiveRoundReady(id);
            Rps previous1 = Rps.Rock;
            Rps previous2 = Rps.NotAMove;
            for (int idx = 0; idx < scenarioGameNumber; ++idx) {
                coordinator().sendCall(id);
                coordinator().setPreviousOppositeMove(id, previous2);
                previous2 = previous1;
                previous1 = coordinator().receiveMove(id);
                coordinator().sendResultUpdate(id);
            }
            coordinator().sendMatch(id);
        }
        coordinator().sendClose(id);
        coordinator().close(id);
    }

    public void testMultipleScenario() throws Exception {
        final int id1 = coordinator().connect();
        coordinator().sendHello(id1);
        final int id2 = coordinator().connect();
        coordinator().setSessionId(id1, "S1");
        coordinator().sendInitiate(id1);
        coordinator().sendHello(id2);
        coordinator().setSessionId(id2, "S2");
        coordinator().sendInitiate(id2);
        final int id3 = coordinator().connect();
        coordinator().close(id3);
        coordinator().receiveInitiate(id2);
        coordinator().receiveInitiate(id1);
        for (int round = 0; round < 2; ++round) {
            coordinator().setRoundId(id2, String.format("R_%d", round));
            coordinator().sendRoundReady(id2);
            coordinator().setRoundId(id1, String.format("R_%d", round));    // same as S2
            coordinator().sendRoundReady(id1);
            coordinator().receiveRoundReady(id1);
            coordinator().receiveRoundReady(id2);
            for (int idx = 0; idx < 5; ++idx) {
                coordinator().sendCall(id1);
                coordinator().sendCall(id2);
                coordinator().receiveMove(id2);
                coordinator().sendResultUpdate(id2);
                coordinator().receiveMove(id1);
                coordinator().sendResultUpdate(id1);
            }
            coordinator().sendMatch(id2);
            coordinator().sendMatch(id1);
        }
        final int id4 = coordinator().connect();
        coordinator().close(id4);
        coordinator().sendClose(id1);
        coordinator().close(id1);
        coordinator().sendClose(id2);
        coordinator().close(id2);
    }

    public void testSendCloseWithoutReady() throws Exception {
        final int id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().sendClose(id);
        coordinator().close(id);
    }

    public void testSendMatchWithoutCall() throws Exception {
        final int id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, String.format("R_%d", 123));
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().sendMatch(id);
        coordinator().sendClose(id);
        coordinator().close(id);
    }

    public void testConnectClose() throws Exception {
        final int id1 = coordinator().connect();
        coordinator().close(id1);
        final int id2 = coordinator().connect();
        coordinator().close(id2);
        final int id3 = coordinator().connect();
        coordinator().close(id3);
    }

    public void testMultipleConnect() throws Exception {
        final int id1 = coordinator().connect();
        final int id2 = coordinator().connect();
        final int id3 = coordinator().connect();
        final int id4 = coordinator().connect();
        final int id5 = coordinator().connect();
        coordinator().close(id3);
        coordinator().close(id2);
        coordinator().close(id5);
        coordinator().close(id1);
        coordinator().close(id4);
    }

    public void testUnexpectedClose() throws Exception {
        int id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().sendCall(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().sendCall(id);
        coordinator().receiveMove(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().sendCall(id);
        coordinator().receiveMove(id);
        coordinator().setPreviousOppositeMove(id, Rps.Rock);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().sendCall(id);
        coordinator().receiveMove(id);
        coordinator().setPreviousOppositeMove(id, Rps.Rock);
        coordinator().sendResultUpdate(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().sendCall(id);
        coordinator().receiveMove(id);
        coordinator().setPreviousOppositeMove(id, Rps.Rock);
        coordinator().sendResultUpdate(id);
        coordinator().sendMatch(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().sendHello(id);
        coordinator().setSessionId(id, "S1");
        coordinator().sendInitiate(id);
        coordinator().receiveInitiate(id);
        coordinator().setRoundId(id, "R1");
        coordinator().sendRoundReady(id);
        coordinator().receiveRoundReady(id);
        coordinator().sendCall(id);
        coordinator().receiveMove(id);
        coordinator().setPreviousOppositeMove(id, Rps.Rock);
        coordinator().sendResultUpdate(id);
        coordinator().sendMatch(id);
        coordinator().sendClose(id);
        coordinator().close(id);
        //
        id = coordinator().connect();
        coordinator().close(id);
    }
}
