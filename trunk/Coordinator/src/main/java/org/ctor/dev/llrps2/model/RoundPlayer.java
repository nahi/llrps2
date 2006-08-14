package org.ctor.dev.llrps2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.ctor.dev.llrps2.session.SessionStub;

@Entity
public class RoundPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @OneToOne(mappedBy = "leftPlayer")
    private final Round roundAsLeft;

    @OneToOne(mappedBy = "rightPlayer")
    private final Round roundAsRight;

    @ManyToOne(optional = false)
    private final Agent agent;

    @Column(length = 255)
    private String cname = null;

    @Transient
    private SessionStub session = null;

    static RoundPlayer createAsLeft(Agent agent, Round round) {
        Validate.notNull(agent);
        Validate.notNull(round);
        return new RoundPlayer(agent, round, null);
    }

    static RoundPlayer createAsRight(Agent agent, Round round) {
        Validate.notNull(agent);
        Validate.notNull(round);
        return new RoundPlayer(agent, null, round);
    }

    RoundPlayer() {
        this(null, null, null);
    }

    private RoundPlayer(Agent agent, Round roundAsLeft, Round roundAsRight) {
        this.agent = agent;
        this.roundAsLeft = roundAsLeft;
        this.roundAsRight = roundAsRight;
    }

    public Long getId() {
        return id;
    }

    public Agent getAgent() {
        return agent;
    }

    public void setCname(String cname) {
        Validate.notNull(cname);
        this.cname = cname;
    }

    public String getCname() {
        return cname;
    }

    public void setSession(SessionStub session) {
        Validate.notNull(session);
        this.session = session;
    }

    public SessionStub getSession() {
        return session;
    }

    public Round getRound() {
        if (getRoundAsLeft() != null) {
            return getRoundAsLeft();
        }
        if (getRoundAsRight() == null) {
            throw new IllegalStateException("both are null");
        }
        return getRoundAsRight();
    }

    private Round getRoundAsLeft() {
        return roundAsLeft;
    }

    private Round getRoundAsRight() {
        return roundAsRight;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RoundPlayer)) {
            return false;
        }
        final RoundPlayer rhs = (RoundPlayer) other;
        return new EqualsBuilder().append(getRound(), rhs.getRound()).append(
                getAgent(), rhs.getAgent()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getRound())
                .append(getAgent()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("cname", getCname()).append(
                "agent", getAgent()).toString();
    }
}
