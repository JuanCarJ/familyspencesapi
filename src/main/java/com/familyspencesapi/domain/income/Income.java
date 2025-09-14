
package com.familyspencesapi.domain.income;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "incomes")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrement en PostgreSQL
    private Long id;

    @Column(nullable = false, columnDefinition = "UUID")
    private UUID familyId;

    @Column(nullable = false)
    private String responsible;

    @Column(nullable = false)
    private String title;

    private String description;
    private String period;

    @Column(nullable = false)
    private Double total;

    public Income() {}

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public UUID getFamilyId() { return familyId; }
    public void setFamilyId(UUID familyId) { this.familyId = familyId; }

    public String getResponsible() { return responsible; }
    public void setResponsible(String responsible) { this.responsible = responsible; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
