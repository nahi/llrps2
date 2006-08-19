package org.ctor.dev.llrps2.coordinator;

import java.util.List;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.AgentMessage;
import org.springframework.jms.core.JmsTemplate;

public class AgentManager implements MessageListener {
    private static final Log LOG = LogFactory.getLog(AgentManager.class);

    private final JmsTemplate jmsTemplate;

    private Coordinator coordinator = null;

    private Destination agentEnrollmentRequestDestination = null;

    AgentManager(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void onMessage(Message message) {
        LOG.info("received: connected agent notification message");
        if (!(message instanceof ObjectMessage)) {
            throw new IllegalArgumentException(
                    "Message must be of type ObjectMessage: "
                            + message.getClass());
        }
        try {
            final ObjectMessage obj = (ObjectMessage) message;
            final List<AgentMessage> agents = (List<AgentMessage>) obj
                    .getObject();
            System.out.println("= connected agents =");
            for (AgentMessage agent : agents) {
                System.out.println(agent);
            }
        } catch (JMSException e) {
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void requestAgentEnrollment(AgentMessage agent) {
        jmsTemplate.convertAndSend(agentEnrollmentRequestDestination, agent);
        LOG.info("sent: agent enrollment request for " + agent);
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setAgentEnrollmentRequestDestination(
            Destination agentEnrollmentRequest) {
        this.agentEnrollmentRequestDestination = agentEnrollmentRequest;
    }

    public Destination getAgentEnrollmentRequestDestination() {
        return agentEnrollmentRequestDestination;
    }
}
