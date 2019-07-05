package org.acme.rest.json;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import javax.persistence.*;
import io.quarkus.runtime.annotations.RegisterForReflection;
@RegisterForReflection
@Entity
public class ActorInfo extends PanacheEntity {

    private String firstName;
    private String lastName;
    private boolean organisation;
    private String organisationName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isOrganisation() {
        return organisation;
    }

    public void setOrganisation(boolean organisation) {
        this.organisation = organisation;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }
}