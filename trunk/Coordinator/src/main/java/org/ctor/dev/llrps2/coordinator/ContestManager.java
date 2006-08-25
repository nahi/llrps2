package org.ctor.dev.llrps2.coordinator;

import java.util.Arrays;
import java.util.Iterator;
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

    public void openContest(String contestName, Agent[] agents) {
        openContest(contestName, Arrays.asList(agents));
    }

    public void openContest(String contestName, List<Agent> agents) {
        final Contest contest = getOrCreateContest(contestName, agents);
        // remove orphan rounds if exists
        final Iterator<Round> ite = contest.getRounds().iterator();
        while (ite.hasNext()) {
            final Round round = ite.next();
            if (round.getResult().getFinishDateTime() == null) {
                ite.remove();
                roundManager.removeRound(round);
                contestDao.flush();
            }
        }
        for (Agent contestant : contest.getContestants()) {
            getAgentManager().requestAgentEnrollment(contestant);
        }
    }

    public void startContest(String contestName, int rounds, RoundRule rule) {
        getRoundManager().requestRoundMediations(contestName, rounds, rule);
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
            for (Agent contestant : found.getContestants()) {
                LOG.info("contestant: " + contestant);
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
