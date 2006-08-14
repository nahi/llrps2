package org.ctor.dev.llrps2.model;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContestTest {
    private static ApplicationContext ctx = null;

    private static SessionFactory sessionFactory = null;

    private Session session = null;

    @BeforeClass
    public static void initializeClass() {
        ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
        sessionFactory = (SessionFactory) ctx.getBean("sessionFactory");
    }

    @AfterClass
    public static void terminateClass() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }

    @Before
    public void initialize() {
        session = sessionFactory.openSession();
    }

    @After
    public void terminate() {
        session.close();
    }

    @Test
    public void testDbAccess() throws Exception {
        final Transaction tx = session.beginTransaction();
        clean();
        final int agentCount = 5;
        final int roundCount = 3;
        final int gameCount = 10;
        createAgents(agentCount);
        final Contest contest = Contest.create();
        session.save(contest);
        final List<Agent> agents = session.createCriteria(Agent.class).list();
        // assertEquals(0, session.createCriteria(Round.class).list().size());
        for (int roundIdx = 0; roundIdx < roundCount; ++roundIdx) {
            final Agent leftAgent = agents.get(randomIndex(agents.size()));
            // the right may be the same as the left. never mind.
            final Agent rightAgent = agents.get(randomIndex(agents.size()));
            session.save(leftAgent);
            session.save(rightAgent);
            final RoundPlayer left = RoundPlayer.create(leftAgent);
            final RoundPlayer right = RoundPlayer.create(rightAgent);
            session.save(left);
            session.save(right);
            //
            final RoundRule rule = RoundRule.create(100, GameRule.Normal);
            final Round round = Round.create(contest, left, right, rule);
            session.save(round);
            final RoundResult result = RoundResult.create(round);
            session.save(result);
            session.flush();
            session.refresh(contest);
            assertTrue(contest.getRounds().contains(round));
            session.refresh(left);
            session.refresh(right);
            assertEquals(round, left.getRound());
            assertEquals(round, right.getRound());
            assertEquals(100, round.getRule().getGameCount());
            assertEquals(GameRule.Normal, round.getRule().getGameRule());

            for (int gameIdx = 0; gameIdx < gameCount; ++gameIdx) {
                final Game game = Game.create(gameIdx, round);
                game.setLeftMove(Move.Paper);
                game.setRightMove(Move.NotAMove);
                session.save(game);
                session.flush();
                // select columns from Game for each game.
                session.refresh(round);
                assertTrue(round.getGames().contains(game));
            }
        }
        final List<Round> rounds = session.createCriteria(Round.class).list();
        assertEquals(roundCount, rounds.size());
        assertEquals(100, rounds.get(0).getRule().getGameCount());
        assertEquals(GameRule.Normal, rounds.get(0).getRule().getGameRule());
        tx.commit();
    }

    private void createAgents(int agents) {
        for (int idx = 0; idx < agents; ++idx) {
            final Agent newAgent = Agent.create("192.168.1." + idx);
            newAgent.setName("agent_" + idx);
            session.save(newAgent);
        }
    }

    private int randomIndex(int size) {
        return (int) (Math.random() * size);
    }

    private void clean() {
        for (Contest contest : (List<Contest>) session.createCriteria(
                Contest.class).list()) {
            session.delete(contest);
        }
    }
}
