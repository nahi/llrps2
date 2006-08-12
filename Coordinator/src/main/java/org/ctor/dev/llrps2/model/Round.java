package org.ctor.dev.llrps2.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.validator.NotNull;

@Entity
public class Round {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private final Long id = null;
    
    @ManyToOne @NotNull
    private final Agent left;

    @ManyToOne @NotNull
    private final Agent right;

    Round() {
        // for persister
        this.left = null;
        this.right = null;
    }
    
    Round(Agent left, Agent right) {
        this.left = left;
        this.right = right;
    }

    public Long getId() {
        return id;
    }
    
    public Agent getLeft() {
        return left;
    }

    public Agent getRight() {
        return right;
    }
}
