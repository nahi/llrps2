package org.ctor.dev.llrps2.coordinator;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.CloseMessage;
import org.ctor.dev.llrps2.message.RoundMediationStatusMessage;
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
        LOG.debug("received: round result notification message");
        if (!(message instanceof ObjectMessage)) {
            throw new IllegalArgumentException(
                    "Message must be of type ObjectMessage: "
                            + message.getClass());
        }
        try {
            final Object obj = ((ObjectMessage) message).getObject();
            if (obj instanceof RoundMessage) {
                final RoundMessage round = (RoundMessage) obj;
                getRoundManager().persistRound(round);
            } else if (obj instanceof RoundMediationStatusMessage) {
                final RoundMediationStatusMessage status = (RoundMediationStatusMessage) obj;
                getRoundManager().notifyRoundMediationStatus(status);
            }
        } catch (JMSException e) {
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void requestRoundMediation(RoundMessage message) {
        jmsTemplate.convertAndSend(roundMediationRequestDestination, message);
        LOG.debug("sent: round mediation request");
    }

    void requestStartRoundMediation(String message) {
        final StartMessage startMessage = StartMessage
                .create("start round mediation for " + message);
        jmsTemplate.convertAndSend(roundMediationRequestDestination,
                startMessage);
        LOG.info("sent: start round mediation request");
    }

    void requestCloseRoundMediation(String message) {
        final CloseMessage startMessage = CloseMessage
                .create("close round mediation for " + message);
        jmsTemplate.convertAndSend(roundMediationRequestDestination,
                startMessage);
        LOG.info("sent: close round mediation request");
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
