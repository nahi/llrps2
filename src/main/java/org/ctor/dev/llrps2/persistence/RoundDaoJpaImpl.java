package org.ctor.dev.llrps2.persistence;

import org.ctor.dev.llrps2.model.Round;

public class RoundDaoJpaImpl extends BaseDaoJpaImpl<Round, Long> implements
        RoundDao {

    public RoundDaoJpaImpl() {
        super(Round.class);
    }

    public Round findByName(String name) {
        return singleObject(getJpaTemplate().find(
                "select o from Round o where o.name = ?1", name));
    }
}
