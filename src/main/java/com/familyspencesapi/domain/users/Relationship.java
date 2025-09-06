package com.familyspencesapi.domain.users;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name ="relationships")
public class Relationship {
    @Id
    @GeneratedValue
    private UUID id;
    @Column(nullable = false, unique = true)
    private String type;

    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
