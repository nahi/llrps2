package org.ctor.dev.llrps2.coordinator;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.GameMessage;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.springframework.jms.core.JmsTemplate;

public class RoundManager implements MessageListener {
    private static final Log LOG = LogFactory.getLog(RoundManager.class);

    private final JmsTemplate jmsTemplate;

    private Coordinator coordinator = null;

    private Destination roundMediationRequestDestination = null;

    RoundManager(JmsTemplate jmsTemplate) {
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
            final RoundMessage round = (RoundMessage) obj.getObject();
            System.out.println("= round result =");
            System.out.println(round);
            for (GameMessage game : round.getGames()) {
                System.out.println(game);
            }
        } catch (JMSException e) {
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    
    void requestRoundMediation(RoundMessage round) {
        jmsTemplate.convertAndSend(roundMediationRequestDestination, round);
        LOG.info("sent: round mediation request");
    }
    
    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setRoundMediationRequestDestination(Destination roundMediationRequestDestination) {
        this.roundMediationRequestDestination = roundMediationRequestDestination;
    }

    public Destination getRoundMediationRequestDestination() {
        return roundMediationRequestDestination;
    }
}
