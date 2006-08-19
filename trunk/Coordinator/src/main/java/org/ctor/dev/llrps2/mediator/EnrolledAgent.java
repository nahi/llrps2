package org.ctor.dev.llrps2.mediator;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.ctor.dev.llrps2.message.AgentMessage;

public class EnrolledAgent {

    private final AgentMessage agent;

    private Queue<SessionHandler> sessionHandlers = new LinkedList<SessionHandler>();

    static EnrolledAgent create(AgentMessage agent) {
        Validate.notNull(agent);
        return new EnrolledAgent(agent);
    }

    private EnrolledAgent(AgentMessage agent) {
        this.agent = agent;
    }

    public AgentMessage getAgent() {
        return agent;
    }

    public int connections() {
        return sessionHandlers.size();
    }

    public void addSession(SessionHandler session) {
        sessionHandlers.add(session);
    }

    public SessionHandler pollSession() {
        return sessionHandlers.poll();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof EnrolledAgent)) {
            return false;
        }
        final EnrolledAgent rhs = (EnrolledAgent) other;
        return new EqualsBuilder().append(getAgent(), rhs.getAgent())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getAgent()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("agent", getAgent()).append(
                "connections", connections()).toString();
    }
}