package org.ctor.dev.llrps2.model;

import org.apache.commons.lang.Validate;

public class AgentPair extends Pair<Agent> {
    public static AgentPair create(Agent left, Agent right) {
        Validate.notNull(left);
        Validate.notNull(right);
        return new AgentPair(left, right);
    }
    
    private AgentPair(Agent left, Agent right) {
        super(left, right);
    }
}
