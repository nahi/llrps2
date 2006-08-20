package org.ctor.dev.llrps2.persistence;

import org.ctor.dev.llrps2.model.Agent;

public class AgentDaoJpaImpl extends BaseDaoJpaImpl<Agent, Long> implements
        AgentDao {

    public AgentDaoJpaImpl() {
        super(Agent.class);
    }
}
