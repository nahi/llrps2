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

public class CompetitionTest {
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
        sessionFactory.close();
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
        createAgents(5);
        final Competition competition = Competition.create();
        session.save(competition);
        final int rounds = 5;
        final int games = 20;
        final List<Agent> agents = session.createCriteria(Agent.class).list();
        //assertEquals(0, session.createCriteria(Round.class).list().size());
        for (int roundIdx = 0; roundIdx < rounds; ++roundIdx) {
            final Agent leftAgent = agents.get(randomIndex(agents.size()));
            // the right may be the same as the left. never mind.
            final Agent rightAgent = agents.get(randomIndex(agents.size()));
            session.save(leftAgent);
            session.save(rightAgent);
            final RoundPlayer left = RoundPlayer.create(leftAgent);
            final RoundPlayer right = RoundPlayer.create(rightAgent);
            session.save(left);
            session.save(right);
            final Round round = Round.create(competition, left, right);
            session.save(round);
            session.flush();
            session.refresh(competition);
            assertTrue(competition.getRounds().contains(round));
            session.refresh(left);
            session.refresh(right);
            assertEquals(round, left.getRound());
            assertEquals(round, right.getRound());

            for (int gameIdx = 0; gameIdx < games; ++gameIdx) {
                final Game game = Game.create(gameIdx, round);
                game.setLeftMove(Move.Paper);
                game.setRightMove(Move.NotAMove);
                System.out.println(gameIdx);
                session.save(game);
                session.flush();
                session.refresh(round);
                assertTrue(round.getGames().contains(game));
            }
        }
        assertEquals(rounds, session.createCriteria(Round.class).list().size());
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
        for (Competition competition : (List<Competition>) session
                .createCriteria(Competition.class).list()) {
            session.delete(competition);
        }
    }
}
