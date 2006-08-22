package org.ctor.dev.llrps2.coordinator;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.AgentPair;
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

    public void openContest(String contestName, Agent[] agents) {
        openContest(contestName, Arrays.asList(agents));
    }

    public void openContest(String contestName, List<Agent> agents) {
        final Contest contest = getOrCreateContest(contestName, agents);
        for (Agent contestant : contest.getContestants()) {
            getAgentManager().requestAgentEnrollment(contestant);
        }
    }

    public int startContest(String contestName, String startId, int rounds,
            RoundRule rule) {
        final Contest contest = contestDao.findByName(contestName);
        if (contest == null) {
            throw new IllegalArgumentException("contest not found: "
                    + contestName);
        }
        final List<Agent> contestants = contest.getContestants();
        int count = 0;
        for (int idx = 0; idx < rounds; ++idx) {
            for (int i = 0; i < contestants.size(); ++i) {
                for (int j = i + 1; j < contestants.size(); ++j) {
                    final AgentPair pair = createMatchup(contestants.get(i),
                            contestants.get(j));
                    final Agent left = pair.getFirst();
                    final Agent right = pair.getSecond();
                    final List<Round> matchups = contestDao.findByMatchup(
                            contest, left, right);
                    if (matchups.size() >= rounds) {
                        LOG
                                .info(String
                                        .format(
                                                "no more round mediation needed for '%s' and '%s'",
                                                left.getName(), right.getName()));
                    } else {
                        getRoundManager().requestRoundMediation(contest,
                                startId, left, right, rule);
                        count += 1;
                    }
                }
            }
        }
        LOG.info(String.format("requested %d round mediations", count));
        return count;
    }

    private AgentPair createMatchup(Agent left, Agent right) {
        if (Math.random() > 0.5) {
            return AgentPair.create(left, right);
        } else {
            return AgentPair.create(right, left);
        }
    }

    private Contest getOrCreateContest(String contestName, List<Agent> agents) {
        final Contest found = contestDao.findByName(contestName);
        if (found != null) {
            LOG.info("contest already exists: " + contestName);
            for (Agent agent : agents) {
                if (!found.getContestants().contains(agent)) {
                    LOG.info("added the new contestant: " + agent);
                    found.getContestants().add(agent);
                }
            }
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
