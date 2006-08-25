package org.ctor.dev.llrps2.mediator;

import java.util.LinkedList;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.AgentMessage;

public class EnrolledAgent {
    private static final Log LOG = LogFactory.getLog(EnrolledAgent.class);

    private final AgentMessage agent;

    private LinkedList<SessionHandler> sessionHandlers = new LinkedList<SessionHandler>();

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

    public void pushfrontSession(SessionHandler session) {
        sessionHandlers.add(0, session);
        LOG.info(String.format("pooling %d sessions for %s", sessionHandlers
                .size(), agent.getName()));
    }

    public void pushbackSession(SessionHandler session) {
        sessionHandlers.add(session);
        LOG.info(String.format("pooling %d sessions for %s", sessionHandlers
                .size(), agent.getName()));
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