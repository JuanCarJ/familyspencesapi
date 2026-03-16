package com.familyspencesapi.domain.users;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "family")
public class Family {
    @Id
    @GeneratedValue(strategy =  GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "family_name")
    private String familyName;
    @OneToMany(mappedBy = "family")
    @JsonIgnore
    private List<RegisterUser> users;

    public Family() {

    }
    public Family(String familyName){
        this.familyName = familyName;
    }

    public List<RegisterUser> getUsers() {
        return users;
    }

    public void setUsers(List<RegisterUser> users) {
        this.users = users;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }
}
