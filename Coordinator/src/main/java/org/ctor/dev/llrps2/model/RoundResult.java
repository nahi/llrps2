package org.ctor.dev.llrps2.model;

import java.util.GregorianCalendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
public class RoundResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @OneToOne(optional = false)
    private final Round round;

    @Column
    private GregorianCalendar startDateTime = null;

    @Column
    private GregorianCalendar finishDateTime = null;

    @Column(nullable = false)
    private int leftGames = 0;

    @Column(nullable = false)
    private int rightGames = 0;

    @Column(nullable = false)
    private int drawGames = 0;

    @Column(nullable = false)
    private int maxLeftStraightGames = 0;

    @Column(nullable = false)
    private int maxRightStraightGames = 0;

    static RoundResult create(Round round) {
        Validate.notNull(round);
        return new RoundResult(round);
    }

    RoundResult() {
        this(null);
    }

    private RoundResult(Round round) {
        this.round = round;
    }

    public Long getId() {
        return id;
    }

    public Round getRound() {
        return round;
    }

    public void setStartDateTime(GregorianCalendar startDateTime) {
        Validate.notNull(startDateTime);
        this.startDateTime = startDateTime;
    }

    public GregorianCalendar getStartDateTime() {
        return startDateTime;
    }

    public void setFinishDateTime(GregorianCalendar finishDateTime) {
        Validate.notNull(finishDateTime);
        this.finishDateTime = finishDateTime;
    }

    public GregorianCalendar getFinishDateTime() {
        return finishDateTime;
    }

    public void setLeftGames(int leftGames) {
        Validate.isTrue(leftGames >= 0, "leftGames must be >= 0");
        this.leftGames = leftGames;
    }

    public int getLeftGames() {
        return leftGames;
    }

    public void setRightGames(int rightGames) {
        Validate.isTrue(rightGames >= 0, "rightGames must be >= 0");
        this.rightGames = rightGames;
    }

    public int getRightGames() {
        return rightGames;
    }

    public void setDrawGames(int drawGames) {
        Validate.isTrue(drawGames >= 0, "drawGames must be >= 0");
        this.drawGames = drawGames;
    }

    public int getDrawGames() {
        return drawGames;
    }

    public void setMaxLeftStraightGames(int maxLeftStraightGames) {
        Validate.isTrue(maxLeftStraightGames >= 0,
                "maxLeftStraightGames must be >= 0");
        this.maxLeftStraightGames = maxLeftStraightGames;
    }

    public int getMaxLeftStraightGames() {
        return maxLeftStraightGames;
    }

    public void setMaxRightStraightGames(int maxRightStraightGames) {
        Validate.isTrue(maxRightStraightGames >= 0,
                "maxRightStraightGames must be >= 0");
        this.maxRightStraightGames = maxRightStraightGames;
    }

    public int getMaxRightStraightGames() {
        return maxRightStraightGames;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RoundResult)) {
            return false;
        }
        final RoundResult rhs = (RoundResult) other;
        return new EqualsBuilder().append(getRound(), rhs.getRound())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getRound()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("startDateTime",
                getStartDateTime()).append("finishDateTime",
                getFinishDateTime()).append("leftGames", getLeftGames())
                .append("rightGames", getRightGames()).append("drawGames",
                        getDrawGames()).append("maxLeftStraightGames",
                        getMaxLeftStraightGames()).append(
                        "maxRightStraightGames", getMaxRightStraightGames())
                .toString();
    }
}
