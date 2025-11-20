package com.familyspencesapi.domain.expense;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    @Column(nullable = false, length = 100)
    private String title;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String description;

    @NotBlank(message = "El período es obligatorio")
    @Pattern(regexp = "^(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre|\\d{4}-\\d{2})$",
            message = "El período debe ser un mes válido en español o formato YYYY-MM")
    @Column(nullable = false, length = 50)
    private String period;

    // CAMBIO: Ahora usa RegisterUser en lugar de FamilyMemberDomain
    @Column(nullable = false, length = 50)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private String responsible;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor que 0")
    @DecimalMax(value = "999999999.99", message = "El valor excede el límite permitido")
    @Digits(integer = 9, fraction = 2, message = "Formato de valor inválido (máximo 9 enteros y 2 decimales)")
    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal value;

    @NotNull(message = "La categoría es obligatoria")
    @Column(nullable = false)
    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private UUID familyId;

    // Constructor vacío (requerido por JPA)
    public Expense() {
    }

    public Expense(UUID id) {
        this.id = id;
    }

    // Constructor completo para datos existentes
    public Expense(UUID id, String title, String description, String period,
                   String responsible, BigDecimal value, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.period = period;
        this.responsible = responsible;
        this.value = value;
        this.category = category;
    }

    // Constructor para nuevos gastos (sin ID)
    public Expense(String title, String description, String period,
                   String responsible, BigDecimal value, String category, UUID familyId) {
        this.title = title;
        this.description = description;
        this.period = period;
        this.responsible = responsible;
        this.value = value;
        this.category = category;
        this.familyId = familyId;
    }



    // Constructor mínimo para gastos básicos
    public Expense(String title, String period, String responsible,
                   BigDecimal value, String category) {
        this.title = title;
        this.period = period;
        this.responsible = responsible;
        this.value = value;
        this.category = category;
    }

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getResponsible() {
        return responsible;
    }

    public void setResponsible(String responsible) {
        this.responsible = responsible;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    // Validación personalizada para el período
    public boolean isValidPeriod() {
        if (period == null || period.isBlank()) {
            return false;
        }

        // Verificar formato YYYY-MM
        if (period.matches("\\d{4}-\\d{2}")) {
            String[] parts = period.split("-");
            int month = Integer.parseInt(parts[1]);
            return month >= 1 && month <= 12;
        }

        // Verificar nombres de meses en español
        String[] validMonths = {"enero", "febrero", "marzo", "abril", "mayo", "junio",
                "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"};

        for (String validMonth : validMonths) {
            if (validMonth.equalsIgnoreCase(period)) {
                return true;
            }
        }

        return false;
    }

    // Método equals mejorado
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Expense)) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}