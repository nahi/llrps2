package org.ctor.dev.llrps2.model;

import static org.junit.Assert.*;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.BeforeClass;
import org.junit.Test;

public class AgentTest {
    private static SessionFactory sessionFactory = null;

    @BeforeClass
    public static void setUp() {
        sessionFactory = new AnnotationConfiguration().configure()
                .buildSessionFactory();
    }

    @Test
    public void test1() {
        final Session session = sessionFactory.openSession();
        final List<Agent> list = session.createCriteria(Agent.class).list();
        System.out.println("---");
        for (Agent agent : list) {
            System.out.println(String.format("%d: %s %s", agent.getId(), agent
                    .getName(), agent.getIpAddress()));
        }
        final Transaction xaction = session.beginTransaction();
        final Agent newAgent = new Agent("192.168.1.1");
        session.save(newAgent);
        xaction.commit();
        System.out.println("---");
        session.close();
        assertTrue(true);
    }
}
