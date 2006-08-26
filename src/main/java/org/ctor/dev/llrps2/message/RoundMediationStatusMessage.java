package org.ctor.dev.llrps2.message;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;

public class RoundMediationStatusMessage implements Serializable {
    private static final long serialVersionUID = 1;

    private int mediatedRounds = 0;

    private int waitingRounds = 0;

    public static RoundMediationStatusMessage create() {
        return new RoundMediationStatusMessage();
    }

    private RoundMediationStatusMessage() {
        //
    }

    public void incrementMediatedRounds() {
        mediatedRounds += 1;
    }

    public int getMediatedRounds() {
        return mediatedRounds;
    }

    public void setWaitingRounds(int waitingRounds) {
        this.waitingRounds = waitingRounds;
    }

    public int getWaitingRounds() {
        return waitingRounds;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("mediatedRounds",
                getMediatedRounds())
                .append("waitingRounds", getWaitingRounds()).toString();
    }
}