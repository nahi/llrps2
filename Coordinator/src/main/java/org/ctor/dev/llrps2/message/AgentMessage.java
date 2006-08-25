package org.ctor.dev.llrps2.message;

import java.io.Serializable;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class AgentMessage implements Serializable {
    private static final long serialVersionUID = 1;

    private final String name;

    private final String ipAddress;

    private final Integer port;

    private final boolean active;
    
    private final Integer decoyType;

    private String cname = null;

    public static AgentMessage create(String name, String ipAddress,
            Integer port, boolean active, Integer decoyType) {
        Validate.notNull(name);
        Validate.notNull(ipAddress);
        return new AgentMessage(name, ipAddress, port, active, decoyType);
    }

    private AgentMessage(String name, String ipAddress, Integer port,
            boolean active, Integer decoyType) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.active = active;
        this.decoyType = decoyType;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getPort() {
        return port;
    }

    /**
     * true if this agent is active (creates connection to mediator).
     * 
     * @return
     */
    public boolean isActive() {
        return active;
    }

    public Integer getDecoyType() {
        return decoyType;
    }
    
    public boolean isDecoy() {
        return decoyType != null;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public String getCname() {
        return cname;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof AgentMessage)) {
            return false;
        }
        final AgentMessage rhs = (AgentMessage) other;
        return new EqualsBuilder().append(getName(), rhs.getName()).append(
                getIpAddress(), rhs.getIpAddress()).append(getPort(),
                rhs.getPort()).append(isActive(), rhs.isActive()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getName()).append(
                getIpAddress()).append(getPort()).append(isActive())
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append(
                "ipAddress", getIpAddress()).append("port", getPort()).append(
                "active", isActive()).append("cname", getCname()).toString();
    }
}