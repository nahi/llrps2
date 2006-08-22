package org.ctor.dev.llrps2.persistence;

import org.ctor.dev.llrps2.model.RoundPlayer;

public class RoundPlayerDaoJpaImpl extends BaseDaoJpaImpl<RoundPlayer, Long> implements
        RoundPlayerDao {

    public RoundPlayerDaoJpaImpl() {
        super(RoundPlayer.class);
    }
}
