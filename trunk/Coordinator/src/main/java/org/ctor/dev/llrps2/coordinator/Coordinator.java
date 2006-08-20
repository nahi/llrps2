package org.ctor.dev.llrps2.coordinator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.AgentMapper;
import org.ctor.dev.llrps2.message.AgentMessage;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.model.Contest;
import org.ctor.dev.llrps2.model.GameRule;
import org.ctor.dev.llrps2.model.RoundRule;
import org.ctor.dev.llrps2.persistence.ContestDao;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Coordinator {
    private static final Log LOG = LogFactory.getLog(Coordinator.class);

    private AgentManager2 agentManager = null;

    private RoundManagers roundManager = null;

    private ContestDao contestDao = null;

    public static void main(String[] args) throws IOException {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:org/ctor/dev/llrps2/coordinator/applicationContext.xml");
        final Coordinator mgr = (Coordinator) ctx.getBean("coordinator");
        System.out.println("coordinator started");
        mgr.start();
    }

    public List<Agent> addAgents() {
        final List<Agent> ary = new ArrayList<Agent>();
        ary.add(getAgentManager().addPassiveAgent("ruby server1", "127.0.0.1", 12347));
        ary.add(getAgentManager().addPassiveAgent("ruby server2", "127.0.0.1", 12348));
        ary.add(getAgentManager().addPassiveAgent("java server", "127.0.0.1", 12346));
        return ary;
    }

    public void start() throws IOException {
        final RoundRule rule = RoundRule.create(50, GameRule.Normal);
        int counter = 0;
        final List<Agent> agents = addAgents();
        final Contest contest = Contest.create("c_1");
        contestDao.save(contest);
        contestDao.flush();
        while (true) {
            System.out.println("agent request: send ?");
            readLineFromConsole();
            for (Agent agent : agents) {
                agentManager.requestAgentEnrollment(agent);
            }
            System.out.println("sent agent(s)");

            System.out.println("round request: send ?");
            readLineFromConsole();
            for (int idx = 0; idx < 5; ++idx) {
                getRoundManager().requestRoundMediation(contest,
                        "r_" + ++counter, agents.get(0), agents.get(1), rule);

                getRoundManager().requestRoundMediation(contest,
                        "r_" + ++counter, agents.get(1), agents.get(2), rule);

                getRoundManager().requestRoundMediation(contest,
                        "r_" + ++counter, agents.get(2), agents.get(0), rule);
            }
            System.out.println("sent round");
        }
    }

    private static String readLineFromConsole() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        return reader.readLine();
    }

    public void setAgentManager(AgentManager2 agentManager) {
        this.agentManager = agentManager;
    }

    public AgentManager2 getAgentManager() {
        return agentManager;
    }

    public void setRoundManager(RoundManagers roundManager) {
        this.roundManager = roundManager;
    }

    public RoundManagers getRoundManager() {
        return roundManager;
    }

    public void setContestDao(ContestDao contestDao) {
        this.contestDao = contestDao;
    }

    public ContestDao getContestDao() {
        return contestDao;
    }
}
