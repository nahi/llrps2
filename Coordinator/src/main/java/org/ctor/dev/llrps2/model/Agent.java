package org.ctor.dev.llrps2.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.apache.commons.lang.Validate;
import org.hibernate.validator.NotNull;

@Entity
public class Agent {
    @Id @GeneratedValue(strategy=GenerationType.AUTO)
    private final Long id = null;
    
    @Column
    private String name = null;

    @Column @NotNull
    private final String ipAddress;

    private Agent() {
        // for persister
        this.ipAddress = null;
    }
    
    Agent(String ipAddress) {
        Validate.notNull(ipAddress, "ipAddress");
        this.ipAddress = ipAddress;
    }
    
    public Long getId() {
        return id;
    }
    
    void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getIpAddress() {
        return ipAddress;
    }
}
