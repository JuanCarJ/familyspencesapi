package com.familyspencesapi.domain.users;


import java.util.UUID;

public class DocumentType {
    private UUID id;
    private String type;

    public DocumentType(UUID id, String type) {
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