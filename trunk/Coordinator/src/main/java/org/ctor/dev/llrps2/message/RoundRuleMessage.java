package org.ctor.dev.llrps2.message;

import java.io.Serializable;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.ctor.dev.llrps2.model.GameRule;

public class RoundRuleMessage implements Serializable {
    private static final long serialVersionUID = 1;

    private final int gameCount;

    private final GameRule gameRule;

    public static RoundRuleMessage create(int gameCount, GameRule gameRule) {
        Validate.isTrue(gameCount > 0);
        Validate.notNull(gameRule);
        return new RoundRuleMessage(gameCount, gameRule);
    }

    private RoundRuleMessage(int gameCount, GameRule gameRule) {
        this.gameCount = gameCount;
        this.gameRule = gameRule;
    }

    public int getGameCount() {
        return gameCount;
    }

    public GameRule getGameRule() {
        return gameRule;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RoundRuleMessage)) {
            return false;
        }
        final RoundRuleMessage rhs = (RoundRuleMessage) other;
        return new EqualsBuilder().append(getGameCount(), rhs.getGameCount())
                .append(getGameRule(), rhs.getGameRule()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getGameCount()).append(
                getGameRule()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("gameCount", getGameCount())
                .append("gameRule", getGameRule()).toString();
    }
}