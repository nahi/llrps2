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

public class AgentTest {
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
        for (int idx = 0; idx < 5; ++idx) {
            final Agent newAgent = new Agent("192.168.1." + idx);
            newAgent.setName("agent_" + idx);
            session.save(newAgent);
        }
        assertEquals(5, session.createCriteria(Agent.class).list().size());
        xaction.commit();
    }

    private void clean() {
        for (Agent agent : (List<Agent>) session.createCriteria(Agent.class)
                .list()) {
            session.delete(agent);
        }
    }
}
