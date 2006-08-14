package org.ctor.dev.llrps2.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "gameNumber",
        "round_id" }) })
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @Column(name = "gameNumber", nullable = false)
    private final Integer gameNumber;

    @Column
    private Move leftMove = Move.NotAMove;

    @Column
    private Move rightMove = Move.NotAMove;

    @Column
    private Integer judge = 0;

    @ManyToOne(optional = false)
    private final Round round;

    static Game create(Integer gameNumber, Round round) {
        Validate.notNull(gameNumber);
        Validate.notNull(round);
        final Game game = new Game(gameNumber, round);
        round.getGames().add(game);
        return game;
    }

    Game() {
        this(null, null);
    }

    private Game(Integer gameNumber, Round round) {
        this.gameNumber = gameNumber;
        this.round = round;
    }

    public Long getId() {
        return id;
    }

    public Integer getGameNumber() {
        return gameNumber;
    }

    public void setLeftMove(Move leftMove) {
        Validate.notNull(leftMove);
        this.leftMove = leftMove;
    }

    public Move getLeftMove() {
        return leftMove;
    }

    public void setRightMove(Move rightMove) {
        Validate.notNull(rightMove);
        this.rightMove = rightMove;
    }

    public Move getRightMove() {
        return rightMove;
    }

    public Round getRound() {
        return round;
    }

    void setJudge(Integer judge) {
        Validate.notNull(judge);
        Validate.isTrue(judge >= -1 && judge <= 1);
        this.judge = judge;
    }

    public Integer getJudge() {
        return judge;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Game)) {
            return false;
        }
        final Game rhs = (Game) other;
        return new EqualsBuilder().append(getRound(), rhs.getRound()).append(
                getGameNumber(), rhs.getGameNumber()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getRound()).append(
                getGameNumber()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("gameNumber", getGameNumber())
                .append("leftMove", getLeftMove()).append("rightMove",
                        getRightMove()).append("judge", getJudge()).toString();
    }

    // XXX check performance and consider to cache it by myself
    public Move getPreviousLeftMove() {
        final Game previousGame = getPreviousGame();
        if (previousGame == null) {
            return Move.NotAMove;
        }
        return previousGame.getLeftMove();
    }

    // XXX check performance and consider to cache it by myself
    public Move getPreviousRightMove() {
        final Game previousGame = getPreviousGame();
        if (previousGame == null) {
            return Move.NotAMove;
        }
        return previousGame.getRightMove();
    }

    private Game getPreviousGame() {
        final List<Game> games = getRound().getGames();
        final int idx = games.indexOf(this);
        if (idx < 0) {
            throw new IllegalStateException("orphan game?");
        }
        if (idx == 0) {
            return null;
        }
        return games.get(idx - 1);
    }
}
