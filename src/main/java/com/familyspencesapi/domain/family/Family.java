package com.familyspencesapi.domain.family;

import jakarta.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
public class Family {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL)
    private List<FamilyMemberDomain> members;

    // === Getters y setters ===
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<FamilyMemberDomain> getMembers() {
        return members;
    }

    public void setMembers(List<FamilyMemberDomain> members) {
        this.members = members;
    }
}
