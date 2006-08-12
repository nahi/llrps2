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
import org.ctor.dev.llrps2.session.SessionStub;

@Entity
public class RoundPlayer {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @OneToOne(mappedBy="left")
    private Round roundAsLeft = null;
    
    @OneToOne(mappedBy="right")
    private Round roundAsRight = null;
    
    @ManyToOne(optional = false)
    private final Agent agent;

    @Column
    private String cname = null;

    @Transient
    private SessionStub session = null;

    static RoundPlayer create(Agent agent) {
        Validate.notNull(agent);
        return new RoundPlayer(agent);
    }

    RoundPlayer() {
        this(null);
    }

    private RoundPlayer(Agent agent) {
        this.agent = agent;
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
    
    void setRoudAsLeft(Round roundAsLeft) {
        this.roundAsLeft = roundAsLeft;
    }
    
    Round getRoundAsLeft() {
        return roundAsLeft;
    }
    
    void setRoundAsRight(Round roundAsRight) {
        this.roundAsRight = roundAsRight;
    }

    Round getRoundAsRight() {
        return roundAsRight;
    }
}
