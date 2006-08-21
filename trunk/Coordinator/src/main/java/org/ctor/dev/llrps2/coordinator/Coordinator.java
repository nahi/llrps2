package org.ctor.dev.llrps2.coordinator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.Contest;
import org.ctor.dev.llrps2.model.GameRule;
import org.ctor.dev.llrps2.model.RoundRule;
import org.ctor.dev.llrps2.persistence.ContestDao;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Coordinator {
    private static final Log LOG = LogFactory.getLog(Coordinator.class);

    private AgentManager agentManager = null;

    private RoundManager roundManager = null;

    private ContestDao contestDao = null;

    public static void main(String[] args) throws IOException {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:org/ctor/dev/llrps2/coordinator/applicationContext.xml");
        final Coordinator mgr = (Coordinator) ctx.getBean("coordinator");
        LOG.info("coordinator started");
        mgr.start();
    }

    public List<Agent> addAgents() {
        final List<Agent> ary = new ArrayList<Agent>();
        ary.add(getAgentManager().addPassiveAgent("ruby server1", "127.0.0.1",
                12347));
        ary.add(getAgentManager().addPassiveAgent("ruby server2", "127.0.0.1",
                12348));
        ary.add(getAgentManager().addPassiveAgent("java server", "127.0.0.1",
                12346));
        ary.add(getAgentManager().addActiveAgent("awk client", "127.0.0.1"));
        return ary;
    }

    public void start() throws IOException {
        final int rounds = 5;
        final RoundRule rule = RoundRule.create(50, GameRule.Normal);
        final List<Agent> agents = addAgents();
        while (true) {
            System.out.println("contest name ?");
            final String contestName = readLineFromConsole();
            final Contest contest = Contest.create(contestName);
            for (Agent agent : agents) {
                agentManager.requestAgentEnrollment(agent);
            }
            contestDao.save(contest);
            contestDao.flush();
            System.out.println("agent request sent");

            System.out.println("round request: send ?");
            readLineFromConsole();
            int counter = 0;
            for (int idx = 0; idx < rounds; ++idx) {
                // all combination
                for (int i = 0; i < agents.size(); ++i) {
                    for (int j = i + 1; j < agents.size(); ++j) {
                        getRoundManager().requestRoundMediation(contest,
                                contestName + "_R" + ++counter, agents.get(i),
                                agents.get(j), rule);
                    }
                }
            }
            System.out.println("sent round");
        }
    }

    private static String readLineFromConsole() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        return reader.readLine();
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
}
