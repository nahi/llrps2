package org.ctor.dev.llrps2.persistence;

import org.ctor.dev.llrps2.model.Contest;

public class ContestDaoJpaImpl extends BaseDaoJpaImpl<Contest, Long> implements
        ContestDao {

    public ContestDaoJpaImpl() {
        super(Contest.class);
    }
}
