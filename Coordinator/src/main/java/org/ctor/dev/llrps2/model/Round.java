package org.ctor.dev.llrps2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import org.apache.commons.lang.Validate;

@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @ManyToOne(optional = false)
    private Competition competition = null;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private final RoundPlayer left;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private final RoundPlayer right;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "round")
    @OrderBy("gameNumber")
    private final List<Game> games;

    static Round create(Competition competition, RoundPlayer left,
            RoundPlayer right) {
        Validate.notNull(competition);
        Validate.notNull(left);
        Validate.notNull(right);
        return new Round(competition, left, right);
    }

    Round() {
        this(null, null, null);
    }

    private Round(Competition competition, RoundPlayer left, RoundPlayer right) {
        this.competition = competition;
        this.left = left;
        this.right = right;
        this.games = new ArrayList<Game>();
    }

    public Long getId() {
        return id;
    }
    
    public void setCompetition(Competition competition) {
        this.competition = competition;
    }

    public Competition getCompetition() {
        return competition;
    }

    public RoundPlayer getLeft() {
        return left;
    }

    public RoundPlayer getRight() {
        return right;
    }

    public List<Game> getGames() {
        return games;
    }
}
