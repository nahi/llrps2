package org.ctor.dev.llrps2.model;

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
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @Column
    private String name = null;

    @Column(nullable = false, unique = true)
    private final String ipAddress;

    static Agent create(String ipAddress) {
        Validate.notNull(ipAddress);
        return new Agent(ipAddress);
    }

    Agent() {
        this(null);
    }

    private Agent(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Long getId() {
        return id;
    }

    void setName(String name) {
        Validate.notNull(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
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
        return new EqualsBuilder().append(getIpAddress(), rhs.getIpAddress())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(getIpAddress()).toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("name", getName()).append(
                "ipAddress", getIpAddress()).toString();
    }
}
