package org.ctor.dev.llrps2.persistence;

import java.util.List;

import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.Contest;
import org.ctor.dev.llrps2.model.Round;

public class ContestDaoJpaImpl extends BaseDaoJpaImpl<Contest, Long> implements
        ContestDao {

    public ContestDaoJpaImpl() {
        super(Contest.class);
    }

    public Contest findByName(String name) {
        return singleObject(getJpaTemplate().find(
                "select o from Contest o where o.name = ?1", name));
    }

    public List<Round> findByMatchUp(Contest contest, Agent left, Agent right) {
        return getJpaTemplate().find(
                "select o from Round o where o.contest = ?1 and "
                        + "((o.leftPlayer = ?2 and o.rightPlayer = ?3) or "
                        + "(o.rightPlayer = ?2 and o.leftPlayer = ?3))",
                contest, left, right);
    }
}
