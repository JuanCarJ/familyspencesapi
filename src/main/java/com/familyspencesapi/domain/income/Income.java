package com.familyspencesapi.domain.income;

import jakarta.persistence.*; // Importa las anotaciones JPA
import java.util.UUID;

@Entity                     // ✅ Marca esta clase como entidad JPA
@Table(name = "incomes")    // ✅ Nombre de la tabla en la base de datos
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private UUID familyId;

    @Column(nullable = false)
    private String responsible;

    @Column(nullable = false)
    private String title;

    private String description;

    @Column(nullable = false)
    private String period;

    @Column(nullable = false)
    private Double total;

    // ✅ Constructor vacío (requerido por JPA)
    public Income() {}

    // ✅ Getters y Setters
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
