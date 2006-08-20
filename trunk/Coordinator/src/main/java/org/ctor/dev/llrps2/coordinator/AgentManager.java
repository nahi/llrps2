package org.ctor.dev.llrps2.coordinator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.AgentMapper;
import org.ctor.dev.llrps2.message.AgentMessage;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.persistence.AgentDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public class AgentManager {
    private static final Log LOG = LogFactory.getLog(AgentManager.class);

    private AgentConnector agentConnector = null;

    private AgentDao agentDao = null;

    public void showConnectedAgents(final List<AgentMessage> agents) {
        System.out.println("= connected agents =");
        for (AgentMessage agent : agents) {
            System.out.println(agent);
        }
    }

    @Transactional(readOnly = false)
    public Agent addActiveAgent(String agentName, String ipAddress) {
        final Agent agent = Agent.create(agentName, ipAddress, null, true);
        agentDao.save(agent);
        return agent;
    }

    public Agent addPassiveAgent(String agentName, String ipAddress, int port) {
        final Agent agent = Agent.create(agentName, ipAddress, port, false);
        agentDao.save(agent);
        return agent;
    }

    public void requestAgentEnrollment(Agent agent) {
        final AgentMessage agentMessage = AgentMapper.modelToMessage(agent);
        LOG.info("sending agent enrollment request: " + agentMessage);
        getAgentConnector().requestAgentEnrollment(agentMessage);
    }

    public void setAgentConnector(AgentConnector agentConnector) {
        this.agentConnector = agentConnector;
    }

    public AgentConnector getAgentConnector() {
        return agentConnector;
    }

    public void setAgentDao(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    public AgentDao getAgentDao() {
        return agentDao;
    }
}
