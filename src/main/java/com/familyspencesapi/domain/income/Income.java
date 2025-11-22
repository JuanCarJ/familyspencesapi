
package com.familyspencesapi.domain.income;

import com.familyspencesapi.domain.users.RegisterUser;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.UUID;

@Entity
@Table(name = "incomes")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Income {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "El título no puede estar vacío")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "La descripción no puede estar vacía")
    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @NotBlank(message = "El período es obligatorio")
    @Pattern(
            regexp = "^(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre|\\d{4}-\\d{2})$",
            message = "El período debe ser un mes válido en español o formato YYYY-MM"
    )
    @Column(nullable = false, length = 50)
    private String period;

    @NotNull(message = "El total no puede ser nulo")
    @Positive(message = "El total debe ser mayor que 0")
    @Column(nullable = false)
    private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private RegisterUser responsible;


    @Column(name = "family_id", nullable = false)
    private UUID family;

    public Income() {}

    public Income(String title, String description, String period, Double total, RegisterUser responsible, UUID family) {
        this.title = title;
        this.description = description;
        this.period = period;
        this.total = total;
        this.responsible = responsible;
        this.family = family;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public RegisterUser getResponsible() { return responsible; }
    public void setResponsible(RegisterUser responsible) { this.responsible = responsible; }
    public UUID getFamily() { return family; }
    public void setFamily(UUID family) { this.family = family; }
}
