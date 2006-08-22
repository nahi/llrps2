package org.ctor.dev.llrps2.coordinator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.model.GameRule;
import org.ctor.dev.llrps2.model.RoundRule;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Coordinator {
    private static final Log LOG = LogFactory.getLog(Coordinator.class);

    private AgentManager agentManager = null;

    private ContestManager contestManager = null;

    public static void main(String[] args) throws IOException {
        final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext(
                "classpath:org/ctor/dev/llrps2/coordinator/applicationContext.xml");
        final Coordinator mgr = (Coordinator) ctx.getBean("coordinator");
        LOG.info("coordinator started");
        mgr.start();
    }

    /*
     * a1 = agentManager.createPassiveAgent("java", "127.0.0.1", 12346)
     * 
     * a2 = agentManager.createPassiveAgent("ruby", "127.0.0.1", 12347)
     * 
     * a3 = agentManager.createActiveAgent("gawk", "127.0.0.1")
     * 
     * a = agentManager.getAgent("name")
     * 
     * contestManager.openContest("C1", [a1, a2])
     * 
     * contestManager.startContest("C1", 30, rule)
     */
    public void start() throws IOException {
        final RoundRule rule = RoundRule.create(100, GameRule.Normal);
        final Context ctx = Context.enter();
        try {
            final Scriptable scope = ctx.initStandardObjects();
            ScriptableObject.putProperty(scope, "out", Context.javaToJS(
                    System.out, scope));
            ScriptableObject.putProperty(scope, "rule", Context.javaToJS(rule,
                    scope));
            ScriptableObject.putProperty(scope, "agentManager", Context
                    .javaToJS(agentManager, scope));
            ScriptableObject.putProperty(scope, "contestManager", Context
                    .javaToJS(contestManager, scope));

            ScriptableObject.putProperty(scope, "python", agentManager
                    .getOrCreateActiveAgent("Python", "192.168.1.16"));
            ScriptableObject.putProperty(scope, "javaScript", agentManager
                    .getOrCreateActiveAgent("JavaScript", "192.168.1.17"));
            ScriptableObject.putProperty(scope, "actionScript", agentManager
                    .getOrCreateActiveAgent("ActionScript", "192.168.1.18"));
            ScriptableObject.putProperty(scope, "gawk", agentManager
                    .getOrCreateActiveAgent("gawk", "192.168.1.19"));
            ScriptableObject.putProperty(scope, "ruby", agentManager
                    .getOrCreatePassiveAgent("Ruby", "192.168.1.20", 12346));

            while (true) {
                try {
                    final String line = readLineFromConsole();
                    final Object result = ctx.evaluateString(scope, line,
                            "<cmd>", 1, null);
                    System.out.println(Context.toString(result));
                } catch (RuntimeException re) {
                    System.err.println(re.getMessage());
                    re.printStackTrace(System.err);
                }
            }
        } finally {
            Context.exit();
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

    public void setContestManager(ContestManager contestManager) {
        this.contestManager = contestManager;
    }

    public ContestManager getContestManager() {
        return contestManager;
    }
}