package org.ctor.dev.llrps2.model;

import java.io.Serializable;

import org.apache.commons.lang.Validate;

public class RoundRule implements Serializable {
    private static final long serialVersionUID = 1;

    private final int gameCount;

    private final GameRule gameRule;

    public static RoundRule create(int gameCount, GameRule gameRule) {
        Validate.isTrue(gameCount >= 0, "gameCount must be >= 0");
        return new RoundRule(gameCount, gameRule);
    }

    private RoundRule(int gameCount, GameRule gameRule) {
        this.gameCount = gameCount;
        this.gameRule = gameRule;
    }

    public int getGameCount() {
        return gameCount;
    }

    public GameRule getGameRule() {
        return gameRule;
    }
}