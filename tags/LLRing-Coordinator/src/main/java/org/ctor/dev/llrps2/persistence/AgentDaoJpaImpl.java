package org.ctor.dev.llrps2.persistence;

import org.ctor.dev.llrps2.model.Agent;

public class AgentDaoJpaImpl extends BaseDaoJpaImpl<Agent, Long> implements
        AgentDao {

    public AgentDaoJpaImpl() {
        super(Agent.class);
    }

    public Agent findByName(String name) {
        return singleObject(getJpaTemplate().find(
                "select o from Agent o where o.name = ?1", name));
    }
}