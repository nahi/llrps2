package org.ctor.dev.llrps2.coordinator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.Contest;
import org.ctor.dev.llrps2.model.Round;
import org.ctor.dev.llrps2.model.RoundRule;
import org.ctor.dev.llrps2.persistence.AgentDao;
import org.ctor.dev.llrps2.persistence.ContestDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ContestManager {
    private static final Log LOG = LogFactory.getLog(ContestManager.class);

    private AgentManager agentManager = null;

    private RoundManager roundManager = null;

    private ContestDao contestDao = null;

    private AgentDao agentDao = null;

    void openContest(String contestName, List<Agent> agents) {
        final Contest contest = getOrCreateContest(contestName, agents);
        for (Agent contestant : contest.getContestants()) {
            getAgentManager().requestAgentEnrollment(contestant);
        }
    }

    void startContest(String contestName, int rounds, RoundRule rule) {
        final Contest contest = contestDao.findByName(contestName);
        if (contest == null) {
            throw new IllegalArgumentException("contest not found: "
                    + contestName);
        }
        final List<Agent> contestants = contest.getContestants();
        for (int idx = 0; idx < rounds; ++idx) {
            // all combination
            for (int i = 0; i < contestants.size(); ++i) {
                for (int j = i + 1; j < contestants.size(); ++j) {
                    final List<Round> matchups = contestDao.findByMatchUp(
                            contest, contestants.get(i), contestants.get(j));
                    System.out.println(matchups.size());
                    if (matchups.size() < rounds) {
                        getRoundManager().requestRoundMediation(contest,
                                contestants.get(i), contestants.get(j), rule);
                    }
                }
            }
        }
    }

    private Contest getOrCreateContest(String contestName, List<Agent> agents) {
        final Contest found = contestDao.findByName(contestName);
        if (found != null) {
            LOG.info("contest already exists: " + contestName);
            return found;
        }
        LOG.info("creating contest: " + contestName);
        final Contest contest = Contest.create(contestName);
        for (Agent agent : agents) {
            final Agent contestant = agentDao.findByName(agent.getName());
            if (contestant == null) {
                throw new IllegalArgumentException("no such agent: " + agent);
            }
            contest.getContestants().add(contestant);
        }
        contestDao.save(contest);
        contestDao.flush();
        return contest;
    }

    public void setAgentManager(AgentManager agentManager) {
        this.agentManager = agentManager;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setRoundManager(RoundManager roundManager) {
        this.roundManager = roundManager;
    }

    public RoundManager getRoundManager() {
        return roundManager;
    }

    public void setContestDao(ContestDao contestDao) {
        this.contestDao = contestDao;
    }

    public ContestDao getContestDao() {
        return contestDao;
    }

    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    public AgentDao getAgentDao() {
        return agentDao;
    }
}
