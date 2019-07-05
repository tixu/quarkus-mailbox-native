package org.acme.rest.json;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import io.quarkus.runtime.annotations.RegisterForReflection;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
@RegisterForReflection
@Entity
public class MailBox extends PanacheEntity {
    @Column(unique = true)
    private String name;
    @Column
    private String accessKey;

    @OneToOne(cascade = CascadeType.ALL)
    private ActorInfo actor;

    public MailBox() {
        this.accessKey = UUID.randomUUID().toString();
    }

    public MailBox(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MailBox other = (MailBox) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public ActorInfo getActor() {
        return actor;
    }

    public void setActor(ActorInfo actor) {
        this.actor = actor;
    }

    public static MailBox findByAccessKey(String accessKey) {
        return find("accessKey", accessKey).firstResult();
    }

}