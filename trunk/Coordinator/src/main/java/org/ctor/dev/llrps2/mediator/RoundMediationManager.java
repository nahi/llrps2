package org.ctor.dev.llrps2.mediator;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.springframework.jms.core.JmsTemplate;

public class RoundMediationManager implements MessageListener {
    private static final Log LOG = LogFactory
            .getLog(RoundMediationManager.class);

    private final JmsTemplate jmsTemplate;

    private Mediator mediator = null;

    private String roundResultNotificationDestination = null;

    private final List<RoundMessage> rounds = new CopyOnWriteArrayList<RoundMessage>();

    RoundMediationManager(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void onMessage(Message message) {
        if (!(message instanceof ObjectMessage)) {
            throw new IllegalArgumentException(
                    "Message must be of type ObjectMessage: "
                            + message.getClass());
        }
        try {
            final ObjectMessage obj = (ObjectMessage) message;
            final RoundMessage round = (RoundMessage) obj.getObject();
            LOG.info("received: round mediation request: " + round);
            rounds.add(round);
            mediator.notifyRoundMediationRequest();
        } catch (JMSException e) {
            LOG.warn(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    void notifyRoundResult(RoundMessage round) {
        if (!rounds.contains(round)) {
            LOG.warn("unknown round message: " + round);
            throw new IllegalStateException();
        }
        if (!round.isCompleted()) {
            throw new IllegalStateException("round not finished");
        }
        jmsTemplate.convertAndSend(roundResultNotificationDestination, round);
        LOG.info("sent round result notification: " + round);
        rounds.remove(round);
        mediator.scan();
    }

    public void setMediator(Mediator sessionManager) {
        this.mediator = sessionManager;
    }

    public Mediator getMediator() {
        return mediator;
    }

    public void setRoundResultNotificationDestination(
            String roundResultNotificationDestination) {
        this.roundResultNotificationDestination = roundResultNotificationDestination;
    }

    public String getRoundResultNotificationDestination() {
        return roundResultNotificationDestination;
    }

    public List<RoundMessage> getRounds() {
        return rounds;
    }
}