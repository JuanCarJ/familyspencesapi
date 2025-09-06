package com.familyspencesapi.domain.users;

import jakarta.persistence.*;

import java.util.UUID;

public class Relationship {
    private UUID id;
    private String type;

    public Relationship(UUID id, String type) {
        this.id = id;
        this.type = type;
    }
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
