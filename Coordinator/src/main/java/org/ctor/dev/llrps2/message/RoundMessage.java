package org.ctor.dev.llrps2.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class RoundMessage implements Serializable {
    private static final long serialVersionUID = 1;

    private final String roundId;

    private final RoundRuleMessage rule;

    private final AgentMessage left;

    private final AgentMessage right;

    private boolean assigned = false;

    private String startDateTime = null;

    private String finishDateTime = null;

    private final List<GameMessage> games = new ArrayList<GameMessage>();

    public static RoundMessage create(String roundId, RoundRuleMessage rule,
            AgentMessage left, AgentMessage right) {
        Validate.notNull(roundId);
        Validate.notNull(rule);
        Validate.notNull(left);
        Validate.notNull(right);
        return new RoundMessage(roundId, rule, left, right);
    }

    private RoundMessage(String roundId, RoundRuleMessage rule,
            AgentMessage left, AgentMessage right) {
        this.roundId = roundId;
        this.rule = rule;
        this.left = left;
        this.right = right;
    }

    public String getRoundId() {
        return roundId;
    }

    public RoundRuleMessage getRule() {
        return rule;
    }

    public AgentMessage getLeft() {
        return left;
    }

    public AgentMessage getRight() {
        return right;
    }

    public void setAssigned(boolean assigned) {
        Validate.isTrue(assigned);
        this.assigned = assigned;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setFinishDateTime(String finishDateTime) {
        this.finishDateTime = finishDateTime;
    }

    public String getFinishDateTime() {
        return finishDateTime;
    }

    public List<GameMessage> getGames() {
        return games;
    }

    public void addGame(GameMessage game) {
        games.add(game);
    }

    public boolean isCompleted() {
        return getFinishDateTime() != null;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RoundMessage)) {
            return false;
        }
        final RoundMessage rhs = (RoundMessage) other;
        return new EqualsBuilder().append(getRoundId(), rhs.getRoundId())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getRoundId()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("roundId", getRoundId())
                .append("left", getLeft()).append("right", getRight()).append(
                        "startDateTime", getStartDateTime()).append(
                        "finishDateTime", getFinishDateTime()).toString();
    }
}