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
    private Long id = null;
    
    @Column
    private String name = null;

    @Column @NotNull
    private String ipAddress = null;

    private Agent() {
        // for persister
    }
    
    Agent(String ipAddress) {
        Validate.notNull(ipAddress, "ipAddress");
        this.ipAddress = ipAddress;
    }

    void setId(Long id) {
        this.id = id;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
}
