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

    public List<Round> findByMatchup(Contest contest, Agent left, Agent right) {
        return getJpaTemplate().find(
                "select r " + "from Round r, RoundPlayer lp, "
                        + "RoundPlayer rp, Agent ra, Agent la "
                        + "where r.contest = ?1 and "
                        + "r.leftPlayer = lp and r.rightPlayer = rp and "
                        + "lp.agent = la and rp.agent = ra and "
                        + "la in (?2, ?3) and ra in (?2, ?3) and la != ra",
                contest, left, right);
    }
}
