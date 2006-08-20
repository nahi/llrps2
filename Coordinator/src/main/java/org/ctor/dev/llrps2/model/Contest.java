package org.ctor.dev.llrps2.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
public class Contest implements Serializable {
    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @Column(length = 255, nullable = false, unique = true)
    private final String name;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Contestant", joinColumns = @JoinColumn(name = "contest_id"), inverseJoinColumns = @JoinColumn(name = "agent_id"), uniqueConstraints = { @UniqueConstraint(columnNames = {
            "contest_id", "agent_id" }) })
    private final List<Agent> contestants;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "contest")
    private final ContestResult result;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest")
    @OrderBy("id")
    private final List<Round> rounds;

    public static Contest create(String name) {
        Validate.notNull(name);
        return new Contest(name);
    }

    Contest() {
        this(null);
    }

    private Contest(String name) {
        this.name = name;
        this.contestants = new ArrayList<Agent>();
        this.result = ContestResult.create(this);
        this.rounds = new ArrayList<Round>();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Agent> getContestants() {
        return contestants;
    }

    public ContestResult getResult() {
        return result;
    }

    public List<Round> getRounds() {
        return rounds;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Contest)) {
            return false;
        }
        final Contest rhs = (Contest) other;
        return new EqualsBuilder().append(getName(), rhs.getName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getName()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append(
                "contestants", getContestants()).append("result", getResult())
                .toString();
    }
    
    public void start() {
        getResult().setStartDateTime(new GregorianCalendar());
    }
    
    public void finish() {
        getResult().setFinishDateTime(new GregorianCalendar());
    }
}
