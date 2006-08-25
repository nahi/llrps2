package org.ctor.dev.llrps2.coordinator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.message.StartMessage;
import org.springframework.jms.core.JmsTemplate;

public class RoundConnector implements MessageListener {
    private static final Log LOG = LogFactory.getLog(RoundConnector.class);

    private final JmsTemplate jmsTemplate;

    private RoundManager roundManager = null;

    private String roundMediationRequestDestination = null;

    RoundConnector(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void onMessage(Message message) {
        LOG.info("received: round result notification message");
        if (!(message instanceof ObjectMessage)) {
            throw new IllegalArgumentException(
                    "Message must be of type ObjectMessage: "
                            + message.getClass());
        }
        try {
            final ObjectMessage obj = (ObjectMessage) message;
            final RoundMessage roundMessage = (RoundMessage) obj.getObject();
            getRoundManager().persistRound(roundMessage);
        } catch (JMSException e) {
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void requestRoundMediation(RoundMessage message) {
        jmsTemplate.convertAndSend(roundMediationRequestDestination, message);
        LOG.info("sent: round mediation request");
    }

    void requestStartRoundMediation(String message) {
        final StartMessage startMessage = StartMessage
                .create("start round mediation for " + message);
        jmsTemplate.convertAndSend(roundMediationRequestDestination,
                startMessage);
        LOG.info("sent: start round mediation request");
    }

    public void setRoundMediationRequestDestination(
            String roundMediationRequestDestination) {
        this.roundMediationRequestDestination = roundMediationRequestDestination;
    }

    public String getRoundMediationRequestDestination() {
        return roundMediationRequestDestination;
    }

    public void setRoundManager(RoundManager roundManager) {
        this.roundManager = roundManager;
    }

    public RoundManager getRoundManager() {
        return roundManager;
    }
}
