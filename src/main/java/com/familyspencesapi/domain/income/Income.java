
package com.familyspencesapi.domain.income;

import java.util.UUID;

public class Income {
    private Long id;
    private UUID familyId;
    private String responsible;
    private String title;
    private String description;
    private String period;
    private Double total;

    // Getters y Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public UUID getFamilyId() {
        return familyId;
    }
    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }
    public String getResponsible() {
        return responsible;
    }
    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }
}
