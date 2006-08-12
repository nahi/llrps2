package org.ctor.dev.llrps2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang.Validate;

@Entity
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private final Long id = null;

    @Column
    private String name = null;

    @Column(nullable = false)
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
}
