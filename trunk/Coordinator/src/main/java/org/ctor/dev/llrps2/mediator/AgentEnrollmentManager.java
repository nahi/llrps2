package org.ctor.dev.llrps2.mediator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.AgentMessage;
import org.springframework.jms.core.JmsTemplate;

public class AgentEnrollmentManager implements MessageListener {
    private static final Log LOG = LogFactory
            .getLog(AgentEnrollmentManager.class);

    private final JmsTemplate jmsTemplate;

    private Mediator mediator = null;

    private Destination connectedAgentNotificationDestination = null;

    private final Set<EnrolledAgent> agents = new CopyOnWriteArraySet<EnrolledAgent>();

    AgentEnrollmentManager(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void onMessage(Message message) {
        LOG.info("received: agent enrollment request message");
        if (!(message instanceof ObjectMessage)) {
            throw new IllegalArgumentException(
                    "Message must be of type ObjectMessage: "
                            + message.getClass());
        }
        try {
            final ObjectMessage obj = (ObjectMessage) message;
            // XXX should get AgentEnrollmentRequestMessage instead of plain
            // AgentMessage
            final AgentMessage agent = (AgentMessage) obj.getObject();
            LOG.info("agent: " + agent);
            agents.add(EnrolledAgent.create(agent));
            mediator.notifyAgentEnrollmentRequest();
        } catch (JMSException e) {
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void notifyConnectedAgents() {
        final List<AgentMessage> message = new ArrayList<AgentMessage>();
        for (EnrolledAgent agent : agents) {
            if (agent.connections() > 0) {
                message.add(agent.getAgent());
            }
        }
        jmsTemplate.convertAndSend(connectedAgentNotificationDestination,
                message);
        LOG.info("sent: connected agents notification");
    }

    public void setMediator(Mediator sessionManager) {
        this.mediator = sessionManager;
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setConnectedAgentNotificationDestination(
            Destination connectedAgentNotificationDestination) {
        this.connectedAgentNotificationDestination = connectedAgentNotificationDestination;
    }

    public Destination getConnectedAgentNotificationDestination() {
        return connectedAgentNotificationDestination;
    }

    public Set<EnrolledAgent> getAgents() {
        return agents;
    }

    public EnrolledAgent findAgent(AgentMessage agent) {
        for (EnrolledAgent enrolled : agents) {
            if (enrolled.getAgent().equals(agent)) {
                return enrolled;
            }
        }
        return null;
    }
}
