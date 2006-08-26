package org.ctor.dev.llrps2.model;

import static org.junit.Assert.*;

import org.hibernate.Transaction;
import org.junit.Test;

public class AgentTest extends AbstractTest {
    @Test
    public void testDbAccess() {
        final Transaction tx = session.beginTransaction();
        clean();
        for (int idx = 0; idx < 5; ++idx) {
            final Agent newAgent = Agent.create("agent_" + idx, "192.168.1."
                    + idx, 0, true);
            session.save(newAgent);
        }
        session.flush();
        assertEquals(5, session.createCriteria(Agent.class).list().size());
        tx.commit();
    }
}
