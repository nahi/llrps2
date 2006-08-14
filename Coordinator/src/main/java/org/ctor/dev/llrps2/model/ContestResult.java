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
public class ContestResult {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @OneToOne(optional = false)
    // unique
    private final Contest contest;

    @Column
    private GregorianCalendar startDateTime = null;

    @Column
    private GregorianCalendar finishDateTime = null;

    static ContestResult create(Contest contest) {
        Validate.notNull(contest);
        return new ContestResult(contest);
    }

    ContestResult() {
        this(null);
    }

    private ContestResult(Contest contest) {
        this.contest = contest;
    }

    public Long getId() {
        return id;
    }

    public Contest getContest() {
        return contest;
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

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContestResult)) {
            return false;
        }
        final ContestResult rhs = (ContestResult) other;
        return new EqualsBuilder().append(getContest(), rhs.getContest())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getContest()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("startDateTime",
                getStartDateTime()).append("finishDateTime",
                getFinishDateTime()).toString();
    }
}
