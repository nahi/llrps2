package org.ctor.dev.llrps2.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import org.apache.commons.lang.Validate;

@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @ManyToOne(optional = false)
    private final Contest contest;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private final RoundPlayer leftPlayer;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    private final RoundPlayer rightPlayer;

    @Transient
    private final RoundRule rule;

    @Column(nullable = false)
    private final int scheduledGameCount;

    @Column(nullable = false)
    private final GameRule gameRule;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "round")
    private final RoundResult result = null;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "round")
    @OrderBy("gameNumber")
    private final List<Game> games;

    static Round create(Contest contest, RoundPlayer leftPlayer,
            RoundPlayer rightPlayer, RoundRule rule) {
        Validate.notNull(contest);
        Validate.notNull(leftPlayer);
        Validate.notNull(rightPlayer);
        Validate.notNull(rule);
        return new Round(contest, leftPlayer, rightPlayer, rule);
    }

    Round() {
        this(null, null, null, null);
    }

    private Round(Contest contest, RoundPlayer leftPlayer,
            RoundPlayer rightPlayer, RoundRule rule) {
        this.contest = contest;
        this.leftPlayer = leftPlayer;
        this.rightPlayer = rightPlayer;
        this.rule = rule;

        this.scheduledGameCount = (rule == null) ? 0 : rule.getGameCount();
        this.gameRule = (rule == null) ? null : rule.getGameRule();
        this.games = new ArrayList<Game>();
        // just to avoid unreferenced private member warning.
        assert (this.scheduledGameCount == scheduledGameCount);
        assert (this.gameRule == gameRule);
    }

    public Long getId() {
        return id;
    }

    public Contest getContest() {
        return contest;
    }

    public RoundPlayer getLeftPlayer() {
        return leftPlayer;
    }

    public RoundPlayer getRightPlayer() {
        return rightPlayer;
    }

    public RoundRule getRule() {
        return rule;
    }

    public RoundResult getResult() {
        return result;
    }

    public List<Game> getGames() {
        return games;
    }
}
