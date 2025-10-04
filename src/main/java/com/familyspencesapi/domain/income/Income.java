package com.familyspencesapi.domain.income;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

@Entity
@Table(name = "incomes")
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Autoincrement en PostgreSQL
    private Long id;

    @NotNull(message = "El familyId no puede ser nulo")
    @Column(nullable = false, columnDefinition = "UUID")
    private UUID familyId;

    @NotBlank(message = "El responsable no puede estar vacío")
    @Column(nullable = false)
    private String responsible;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    private String title;

    private String description;

    @Enumerated(EnumType.STRING) // Guardará "MENSUAL", "SEMANAL" o "UNICO" en la BD
    @Column(nullable = false)
    private IncomePeriod period;

    @NotNull(message = "El total no puede ser nulo")
    @Positive(message = "El total debe ser mayor que 0")
    @Column(nullable = false)
    private Double total;


    public Income() {}

    public Income(UUID familyId, String responsible, String title, String description, IncomePeriod period, Double total) {
        this.familyId = familyId;
        this.responsible = responsible;
        this.title = title;
        this.description = description;
        this.period = period;
        this.total = total;
    }


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

    public IncomePeriod getPeriod() { return period; }
    public void setPeriod(IncomePeriod period) { this.period = period; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
