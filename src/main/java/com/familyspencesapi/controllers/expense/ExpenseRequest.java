package com.familyspencesapi.controllers.expense;

import com.familyspencesapi.domain.expense.Expense;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public class ExpenseRequest {
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
    private String title;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    private String description;

    @NotBlank(message = "El período es obligatorio")
    @Pattern(regexp = "^(enero|febrero|marzo|abril|mayo|junio|julio|agosto|septiembre|octubre|noviembre|diciembre|\\d{4}-\\d{2})$",
            message = "El período debe ser un mes válido en español o formato YYYY-MM")
    private String period;

    @NotNull(message = "El valor es obligatorio")
    @DecimalMin(value = "0.01", message = "El valor debe ser mayor que 0")
    @DecimalMax(value = "999999999.99", message = "El valor excede el límite permitido")
    @Digits(integer = 9, fraction = 2, message = "Formato de valor inválido")
    private BigDecimal value;

    @NotNull(message = "La categoría es obligatoria")
    private Expense.ExpenseCategory category;

    public ExpenseRequest() {}

    public ExpenseRequest(String title, String description, String period
            , BigDecimal value, Expense.ExpenseCategory category) {
        this.title = title;
        this.description = description;
        this.period = period;
        this.value = value;
        this.category = category;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getValue() { return value; }
    public void setValue(BigDecimal value) { this.value = value; }

    public Expense.ExpenseCategory getCategory() { return category; }
    public void setCategory(Expense.ExpenseCategory category) { this.category = category; }

}
