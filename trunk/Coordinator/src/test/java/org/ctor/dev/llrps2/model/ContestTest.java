package org.ctor.dev.llrps2.model;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Transaction;
import org.junit.Test;

public class ContestTest extends AbstractTest {
    @Test
    public void testDbAccess() throws Exception {
        final Transaction tx = session.beginTransaction();
        clean();
        final int agentCount = 5;
        final int roundCount = 3;
        final int gameCount = 10;
        createAgents(agentCount);
        final Contest contest = Contest.create("testContest");
        session.save(contest);
        assertNull(contest.getResult().getStartDateTime());
        assertNull(contest.getResult().getFinishDateTime());
        contest.start();
        final List<Agent> agents = session.createCriteria(Agent.class).list();
        for (int roundIdx = 0; roundIdx < roundCount; ++roundIdx) {
            // left and right may be the same. never mind.
            final Agent left = agents.get(randomIndex(agents.size()));
            final Agent right = agents.get(randomIndex(agents.size()));
            //
            final RoundRule rule = RoundRule.create(gameCount, GameRule.Normal);
            final String roundName = String.format("%s - %s #%d", left
                    .getIpAddress(), right.getIpAddress(), roundIdx + 1);
            final Round round = Round.create(contest, roundName, left, right,
                    rule);
            session.save(round);
            // round is added to contest.rounds automatically
            assertTrue(contest.getRounds().contains(round));
            assertEquals(round, round.getLeftPlayer().getRound());
            assertEquals(round, round.getRightPlayer().getRound());
            assertEquals(gameCount, round.getRule().getGameCount());
            assertEquals(GameRule.Normal, round.getRule().getGameRule());
            for (int gameIdx = 0; gameIdx < gameCount; ++gameIdx) {
                final Game game = Game.create(gameIdx, round);
                game.setLeftMove(Move.Paper);
                game.setRightMove(Move.NotAMove);
                assertTrue(round.getGames().contains(game));
            }
            // check if persistence works
            session.flush();
            session.refresh(contest);
            assertTrue(contest.getRounds().contains(round));
            assertEquals(round, round.getLeftPlayer().getRound());
            assertEquals(round, round.getRightPlayer().getRound());
            assertEquals(gameCount, round.getRule().getGameCount());
            assertEquals(GameRule.Normal, round.getRule().getGameRule());
        }
        contest.finish();
        assertNotNull(contest.getResult().getStartDateTime());
        assertNotNull(contest.getResult().getFinishDateTime());
        final List<Round> rounds = session.createCriteria(Round.class).list();
        assertEquals(roundCount, rounds.size());
        assertEquals(gameCount, rounds.get(0).getRule().getGameCount());
        assertEquals(GameRule.Normal, rounds.get(0).getRule().getGameRule());
        tx.commit();
    }
}
