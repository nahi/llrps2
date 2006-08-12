package org.ctor.dev.llrps2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

@Entity
public class Competition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Competitor")
    private final List<Agent> agents;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "competition")
    @OrderBy("id")
    private final List<Round> rounds;

    static Competition create() {
        return new Competition();
    }

    Competition() {
        this.agents = new ArrayList<Agent>();
        this.rounds = new ArrayList<Round>();
    }

    public Long getId() {
        return id;
    }

    public List<Agent> getAgents() {
        return agents;
    }

    public List<Round> getRounds() {
        return rounds;
    }
}
