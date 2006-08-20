package org.ctor.dev.llrps2.coordinator;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.AgentMessage;
import org.springframework.jms.core.JmsTemplate;

public class AgentConnector implements MessageListener {
    private static final Log LOG = LogFactory.getLog(AgentConnector.class);

    private final JmsTemplate jmsTemplate;

    private AgentManager agentManager = null;
    
    private String agentEnrollmentRequestDestination = null;

    AgentConnector(JmsTemplate jmsTemplate) {
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
            getAgentManager().showConnectedAgents(agents);
        } catch (JMSException e) {
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void requestAgentEnrollment(AgentMessage agent) {
        jmsTemplate.convertAndSend(agentEnrollmentRequestDestination, agent);
        LOG.info("sent: agent enrollment request");
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setAgentEnrollmentRequestDestination(
            String agentEnrollmentRequest) {
        this.agentEnrollmentRequestDestination = agentEnrollmentRequest;
    }

    public String getAgentEnrollmentRequestDestination() {
        return agentEnrollmentRequestDestination;
    }
}
