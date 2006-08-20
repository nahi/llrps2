package org.ctor.dev.llrps2.message;

import org.ctor.dev.llrps2.model.Agent;

public final class AgentMapper {
    private AgentMapper() {
        // prohibited
    }

    public static AgentMessage modelToMessage(Agent agent) {
        return AgentMessage.create(agent.getName(), agent.getIpAddress(), agent
                .getPort(), agent.isActive());
    }
}
