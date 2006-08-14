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
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
public class Round {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @ManyToOne(optional = false)
    private final Contest contest;

    @Column(length = 255, nullable = false, unique = true)
    private final String name;

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
    private final RoundResult result;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "round")
    @OrderBy("gameNumber")
    private final List<Game> games;

    static Round create(Contest contest, String name, Agent left, Agent right,
            RoundRule rule) {
        Validate.notNull(contest);
        Validate.notNull(name);
        Validate.notNull(left);
        Validate.notNull(right);
        Validate.notNull(rule);
        final Round round = new Round(contest, name, left, right, rule);
        contest.getRounds().add(round);
        return round;
    }

    Round() {
        this(null, null, null, null, null);
    }

    private Round(Contest contest, String name, Agent left, Agent right,
            RoundRule rule) {
        this.contest = contest;
        this.name = name;
        this.leftPlayer = (left == null) ? null : RoundPlayer.createAsLeft(
                left, this);
        this.rightPlayer = (right == null) ? null : RoundPlayer.createAsRight(
                right, this);
        this.rule = rule;
        this.result = RoundResult.create(this);

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

    public String getName() {
        return name;
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

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Round)) {
            return false;
        }
        final Round rhs = (Round) other;
        return new EqualsBuilder().append(getContest(), rhs.getContest())
                .append(getName(), rhs.getName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getContest()).append(
                getName()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append(
                "leftPlayer", getLeftPlayer()).append("rightPlayer",
                getRightPlayer()).append("rule", getRule()).append("result",
                getResult()).toString();
    }
}
