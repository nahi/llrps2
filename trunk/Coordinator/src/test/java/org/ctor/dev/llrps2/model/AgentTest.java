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

public class AgentTest {
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
    public void testDbAccess() {
        final Transaction tx = session.beginTransaction();
        clean();
        for (int idx = 0; idx < 5; ++idx) {
            final Agent newAgent = Agent.create("192.168.1." + idx);
            newAgent.setName("agent_" + idx);
            session.save(newAgent);
        }
        assertEquals(5, session.createCriteria(Agent.class).list().size());
        tx.commit();
    }

    private void clean() {
        for (Round round : (List<Round>) session.createCriteria(Round.class)
                .list()) {
            session.delete(round);
        }
        for (Agent agent : (List<Agent>) session.createCriteria(Agent.class)
                .list()) {
            session.delete(agent);
        }
    }
}
