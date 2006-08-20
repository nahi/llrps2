package org.ctor.dev.llrps2.coordinator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.ctor.dev.llrps2.message.AgentMessage;
import org.ctor.dev.llrps2.message.RoundMessage;
import org.ctor.dev.llrps2.message.RoundRuleMessage;
import org.ctor.dev.llrps2.model.GameRule;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Coordinator {

    private AgentManager agentManager = null;

    private RoundManager roundManager = null;

    public static void main(String[] args) throws IOException {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:org/ctor/dev/llrps2/coordinator/applicationContext.xml");
        final Coordinator mgr = (Coordinator) ctx.getBean("coordinator");
        System.out.println("coordinator started");
        mgr.start();
    }

    public void start() throws IOException {
        final RoundRuleMessage rule = RoundRuleMessage.create(100,
                GameRule.Normal);
        int counter = 0;
        while (true) {
            System.out.println("agent request: send ?");
            readLineFromConsole();
//            final AgentMessage awkAgent = AgentMessage.create("awk client",
//                    "127.0.0.1", 0, true);
//            agentManager.requestAgentEnrollment(awkAgent);
            final AgentMessage rubyAgent1 = AgentMessage.create("ruby server1",
                    "127.0.0.1", 12347, false);
            agentManager.requestAgentEnrollment(rubyAgent1);
            final AgentMessage rubyAgent2 = AgentMessage.create("ruby server2",
                    "127.0.0.1", 12348, false);
            agentManager.requestAgentEnrollment(rubyAgent2);
            final AgentMessage javaAgent = AgentMessage.create("java server",
                    "127.0.0.1", 12346, false);
            agentManager.requestAgentEnrollment(javaAgent);
            System.out.println("sent 2 agents");

             System.out.println("round request: send ?");
             readLineFromConsole();
            for (int idx = 0; idx < 20; ++idx) {
//                roundManager.requestRoundMediation(RoundMessage.create("C1_R"
//                        + counter++, rule, awkAgent, javaAgent));
                roundManager.requestRoundMediation(RoundMessage.create("C1_R"
                        + counter++, rule, rubyAgent1, rubyAgent2));
//                roundManager.requestRoundMediation(RoundMessage.create("C1_R"
//                        + counter++, rule, awkAgent, rubyAgent1));
                roundManager.requestRoundMediation(RoundMessage.create("C1_R"
                        + counter++, rule, javaAgent, rubyAgent2));
//                roundManager.requestRoundMediation(RoundMessage.create("C1_R"
//                        + counter++, rule, awkAgent, rubyAgent2));
                roundManager.requestRoundMediation(RoundMessage.create("C1_R"
                        + counter++, rule, javaAgent, rubyAgent1));
            }
            System.out.println("sent round");
        }
    }

    private static String readLineFromConsole() throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(
                System.in));
        return reader.readLine();
    }

    public void setAgentManager(AgentManager contestantManager) {
        this.agentManager = contestantManager;
    }

    public AgentManager getAgentManager() {
        return agentManager;
    }

    public void setRoundManager(RoundManager roundResultManager) {
        this.roundManager = roundResultManager;
    }

    public RoundManager getRoundManager() {
        return roundManager;
    }
}
