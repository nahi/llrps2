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

    public Agent getAgent(String agentName) {
        return agentDao.findByName(agentName);
    }

    public Agent getOrCreateActiveAgent(String agentName, String ipAddress) {
        final Agent found = getAgent(agentName);
        if (found != null) {
            LOG.info("agent already exist (ignored ipAddress)");
            return found;
        }
        return createActiveAgent(agentName, ipAddress);
    }

    public Agent getOrCreatePassiveAgent(String agentName, String ipAddress,
            int port) {
        final Agent found = getAgent(agentName);
        if (found != null) {
            LOG.info("agent already exist (ignored ipAddress and port)");
            return found;
        }
        return createPassiveAgent(agentName, ipAddress, port);
    }
    
    public Agent getOrCreateDecoyAgent(String agentName, int decoyType) {
        final Agent found = getAgent(agentName);
        if (found != null) {
            LOG.info("agent already exist (ignored ipAddress and port)");
            return found;
        }
        return createDecoyAgent(agentName, decoyType);
    }

    public Agent createActiveAgent(String agentName, String ipAddress) {
        final Agent agent = Agent.create(agentName, ipAddress, null, true);
        agentDao.save(agent);
        LOG.info("created agent: " + agentName);
        return agent;
    }

    public Agent createPassiveAgent(String agentName, String ipAddress, int port) {
        final Agent agent = Agent.create(agentName, ipAddress, port, false);
        agentDao.save(agent);
        LOG.info("created agent: " + agentName);
        return agent;
    }
    
    public Agent createDecoyAgent(String agentName, int decoyType) {
        final Agent agent = Agent.createDecoy(agentName, decoyType);
        agentDao.save(agent);
        LOG.info("created decoy agent: " + agentName);
        return agent;
    }

    public void showConnectedAgents(final List<AgentMessage> agents) {
        LOG.info(String.format("+-- connected agents (%d) --------", agents
                .size()));
        for (AgentMessage agent : agents) {
            LOG.info("| " + agent);
        }
        LOG.info("+---------------------------------------------------");
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
