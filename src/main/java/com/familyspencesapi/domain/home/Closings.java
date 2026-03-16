package com.familyspencesapi.domain.home;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "closings")
public class Closings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, updatable = false)
    private UUID familyId;

    @Column(nullable = false, updatable = false)
    private LocalDate closingDate;

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal totalIncome;

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal totalExpenses;

    @Column(nullable = false, updatable = false, precision = 19, scale = 4)
    private BigDecimal balance;

    public Closings() {}

    public Closings(UUID familyId, LocalDate closingDate, BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal balance) {
        this.familyId = familyId;
        this.closingDate = closingDate;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.balance = balance;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getFamilyId() { return familyId; }
    public void setFamilyId(UUID familyId) { this.familyId = familyId; }
    public LocalDate getClosingDate() { return closingDate; }
    public void setClosingDate(LocalDate closingDate) { this.closingDate = closingDate; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getTotalExpenses() { return totalExpenses; }
    public void setTotalExpenses(BigDecimal totalExpenses) { this.totalExpenses = totalExpenses; }
    
    @Transient
    private String month;

    public String getMonth() {
        if (closingDate != null) {
            String monthName = closingDate.getMonth().getDisplayName(java.time.format.TextStyle.FULL, new java.util.Locale("es", "ES"));
            return monthName.substring(0, 1).toUpperCase() + monthName.substring(1).toLowerCase();
        }
        return null;
    }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}