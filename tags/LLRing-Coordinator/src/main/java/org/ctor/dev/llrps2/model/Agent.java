package org.ctor.dev.llrps2.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

@Entity
public class Agent implements Serializable {
    private static final String DECOY_IPADDRESS = "127.0.0.1";

    private static final long serialVersionUID = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @Column(length = 255, nullable = false, unique = true)
    private final String name;

    @Column(length = 63, nullable = false)
    private final String ipAddress;

    @Column
    private final Integer port;

    @Column(nullable = false)
    private final boolean active;

    @Column
    private final Integer decoyType;

    public static Agent create(String name, String ipAddress, Integer port,
            boolean active) {
        Validate.notNull(ipAddress);
        Validate.notNull(name);
        return new Agent(name, ipAddress, port, active, null);
    }

    public static Agent createDecoy(String name, Integer decoyType) {
        Validate.notNull(name);
        return new Agent(name, DECOY_IPADDRESS, null, false, decoyType);
    }

    Agent() {
        this(null, null, null, false, null);
    }

    private Agent(String name, String ipAddress, Integer port, boolean active,
            Integer decoyType) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.active = active;
        this.decoyType = decoyType;
    }

    public Long getId() {
        return id;
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

    public boolean isActive() {
        return active;
    }

    public Integer getDecoyType() {
        return decoyType;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Agent)) {
            return false;
        }
        final Agent rhs = (Agent) other;
        return new EqualsBuilder().append(getName(), rhs.getName()).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getName()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append(
                "ipAddress", getIpAddress()).append("port", getPort()).append(
                "active", isActive()).append("decoyType", getDecoyType())
                .toString();
    }
}
