package org.ctor.dev.llrps2.message;

import java.io.Serializable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.ctor.dev.llrps2.model.Move;

public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1;

    private final int gameNumber;

    private Move leftMove = null;

    private Move rightMove = null;

    public static GameMessage create(int gameNumber) {
        return new GameMessage(gameNumber);
    }

    private GameMessage(int gameNumber) {
        this.gameNumber = gameNumber;
    }
    
    public boolean isCompleted() {
        return leftMove != null && rightMove != null;
    }

    public int getGameNumber() {
        return gameNumber;
    }

    public void setLeftMove(Move leftMove) {
        this.leftMove = leftMove;
    }

    public Move getLeftMove() {
        return leftMove;
    }

    public void setRightMove(Move rightMove) {
        this.rightMove = rightMove;
    }

    public Move getRightMove() {
        return rightMove;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof GameMessage)) {
            return false;
        }
        final GameMessage rhs = (GameMessage) other;
        return new EqualsBuilder().append(getGameNumber(), rhs.getGameNumber())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getGameNumber()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("gameNumber", getGameNumber())
                .append("leftMove", getLeftMove()).append("rightMove",
                        getRightMove()).toString();
    }
}