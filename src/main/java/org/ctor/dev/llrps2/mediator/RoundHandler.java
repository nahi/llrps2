package org.ctor.dev.llrps2.mediator;

import java.util.GregorianCalendar;

import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.model.DateTimeMapper;
import org.ctor.dev.llrps2.session.Rps;
import org.ctor.dev.llrps2.session.RpsSessionException;

public abstract class RoundHandler {

    private final RoundMediationManager roundMediationManager;

    private final RoundMessage round;

    private long startDateTime = 0;

    protected int gameNumber = 0;

    protected RoundHandler(RoundMediationManager roundMediationManager,
            RoundMessage round) {
        this.roundMediationManager = roundMediationManager;
        this.round = round;
        setStartDateTime();
    }

    abstract void notifyGameReady(SessionHandler session)
            throws RpsSessionException;

    abstract void notifyMove(SessionHandler session, Rps sessionMove)
            throws RpsSessionException;

    abstract void notifySurrender(SessionHandler session);

    protected boolean expired() {
        final Long timeoutInMillis = round.getRule().getTimeoutInMillis();
        return (timeoutInMillis != null && (startDateTime + timeoutInMillis < now()
                .getTimeInMillis()));
    }

    protected void setStartDateTime() {
        final GregorianCalendar now = now();
        this.startDateTime = now.getTimeInMillis();
        getRound().setStartDateTime(DateTimeMapper.modelToMessage(now));
    }

    protected void setFinishDateTime() {
        final GregorianCalendar now = now();
        getRound().setFinishDateTime(DateTimeMapper.modelToMessage(now));
    }

    private GregorianCalendar now() {
        return new GregorianCalendar();
    }

    public RoundMediationManager getRoundMediationManager() {
        return roundMediationManager;
    }

    public RoundMessage getRound() {
        return round;
    }
}
