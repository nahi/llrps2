package org.ctor.dev.llrps2.model;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class RoundTest {
    private static SessionFactory sessionFactory = null;

    private Session session = null;

    @BeforeClass
    public static void initializeOnce() {
        sessionFactory = new AnnotationConfiguration().configure()
                .buildSessionFactory();
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
    public void testDbAccess() {
        final Transaction xaction = session.beginTransaction();
        clean();
        createAgents(5);
        final int rounds = 15;
        final List<Agent> agents = session.createCriteria(Agent.class).list();
        for (int idx = 0; idx < rounds; ++idx) {
            final Agent left = agents.get(randomIndex(agents.size()));
            // the right may be the same as the left.  never mind.
            final Agent right = agents.get(randomIndex(agents.size()));
            final Round newRound = new Round(left, right);
            session.save(newRound);
        }
        assertEquals(rounds, session.createCriteria(Round.class).list().size());
        xaction.commit();
    }

    private void createAgents(int agents) {
        for (int idx = 0; idx < agents; ++idx) {
            final Agent newAgent = new Agent("192.168.1." + idx);
            newAgent.setName("agent_" + idx);
            session.save(newAgent);
        }
    }
    
    private int randomIndex(int size) {
        return (int)(Math.random() * size);
    }

    private void clean() {
        for (Agent agent : (List<Agent>) session.createCriteria(Agent.class)
                .list()) {
            session.delete(agent);
        }
        for (Round round : (List<Round>) session.createCriteria(Round.class)
                .list()) {
            session.delete(round);
        }
    }
}
