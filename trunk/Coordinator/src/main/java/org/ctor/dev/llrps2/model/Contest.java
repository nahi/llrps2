package org.ctor.dev.llrps2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.UniqueConstraint;

@Entity
public class Contest {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Contestant", joinColumns = @JoinColumn(name = "contest_id"), inverseJoinColumns = @JoinColumn(name = "agent_id"), uniqueConstraints = { @UniqueConstraint(columnNames = {
            "contest_id", "agent_id" }) })
    private final List<Agent> contestants;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "contest")
    @OrderBy("id")
    private final List<Round> rounds;

    static Contest create() {
        return new Contest();
    }

    Contest() {
        this.contestants = new ArrayList<Agent>();
        this.rounds = new ArrayList<Round>();
    }

    public Long getId() {
        return id;
    }

    public List<Agent> getContestants() {
        return contestants;
    }

    public List<Round> getRounds() {
        return rounds;
    }
}
