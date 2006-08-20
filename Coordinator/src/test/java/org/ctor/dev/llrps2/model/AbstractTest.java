package org.ctor.dev.llrps2.model;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public abstract class AbstractTest {
    private static ApplicationContext ctx = null;

    private static SessionFactory sessionFactory = null;

    protected Session session = null;

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

    protected void clean() {
        for (Contest contest : (List<Contest>) session.createCriteria(
                Contest.class).list()) {
            session.delete(contest);
        }
        for (Agent agent : (List<Agent>) session.createCriteria(Agent.class)
                .list()) {
            session.delete(agent);
        }
        session.flush();
    }

    protected void createAgents(int agents) {
        for (int idx = 0; idx < agents; ++idx) {
            final Agent newAgent = Agent.create("agent_" + idx, "192.168.1."
                    + idx, null, false);
            session.save(newAgent);
        }
    }

    protected int randomIndex(int size) {
        return (int) (Math.random() * size);
    }
}
