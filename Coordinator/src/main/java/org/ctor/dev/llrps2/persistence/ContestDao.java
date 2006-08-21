package org.ctor.dev.llrps2.persistence;

import java.util.List;

import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.Contest;
import org.ctor.dev.llrps2.model.Round;

public interface ContestDao extends BaseDao<Contest, Long> {
    Contest findByName(String name);

    List<Round> findByMatchUp(Contest contest, Agent left, Agent right);
}
