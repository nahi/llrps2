package org.ctor.dev.llrps2.coordinator;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ctor.dev.llrps2.message.AgentMapper;
import org.ctor.dev.llrps2.message.AgentMessage;
import org.ctor.dev.llrps2.model.Agent;
import org.ctor.dev.llrps2.persistence.AgentDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class AgentManager {
    private static final Log LOG = LogFactory.getLog(AgentManager.class);

    private AgentConnector agentConnector = null;

    private AgentDao agentDao = null;

    public void showConnectedAgents(final List<AgentMessage> agents) {
        LOG.info("= connected agents =");
        for (AgentMessage agent : agents) {
            LOG.info(agent);
        }
        LOG.info("====================");
    }

    public Agent getOrCreateActiveAgent(String agentName, String ipAddress) {
        final Agent found = agentDao.findByName(agentName);
        if (found != null) {
            LOG.info("agent already exists: " + agentName);
            return found;
        }
        final Agent agent = Agent.create(agentName, ipAddress, null, true);
        agentDao.save(agent);
        LOG.info("created agent: " + agentName);
        return agent;
    }

    public Agent getOrCreatePassiveAgent(String agentName, String ipAddress,
            int port) {
        final Agent found = agentDao.findByName(agentName);
        if (found != null) {
            LOG.info("agent already exists: " + agentName);
            return found;
        }
        final Agent agent = Agent.create(agentName, ipAddress, port, false);
        agentDao.save(agent);
        LOG.info("created agent: " + agentName);
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
