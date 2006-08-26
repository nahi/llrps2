package org.ctor.dev.llrps2.model;

import java.io.Serializable;

import org.apache.commons.lang.Validate;

public class RoundRule implements Serializable {
    private static final long serialVersionUID = 1;

    private final Integer gameCount;

    private final Long timeoutInMillis;

    private final GameRule gameRule;

    public static RoundRule create(Integer gameCount, Long timeoutInMillis,
            GameRule gameRule) {
        Validate.isTrue(gameCount >= 0);
        Validate.isTrue(timeoutInMillis >= 0);
        return new RoundRule(gameCount, timeoutInMillis, gameRule);
    }

    private RoundRule(Integer gameCount, Long timeoutInMillis, GameRule gameRule) {
        this.gameCount = gameCount;
        this.timeoutInMillis = timeoutInMillis;
        this.gameRule = gameRule;
    }

    public Integer getGameCount() {
        return gameCount;
    }

    public Long getTimeoutInMillis() {
        return timeoutInMillis;
    }

    public GameRule getGameRule() {
        return gameRule;
    }
}