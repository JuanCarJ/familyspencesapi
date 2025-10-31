package com.familyspencesapi.domain.ranking;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;
@Entity
@Table(name="ranking")
public class  Ranking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID familyId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_full_name", nullable = false)
    private String fullName;

    @Column(nullable = false, length = 7)
    private String period;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalExpenses;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalIncome;



    public Ranking( UUID familyId, UUID userId, String fullName, String period, BigDecimal totalExpenses, BigDecimal totalIncome) {
        this.familyId = familyId;
        this.userId = userId;
        this.fullName = fullName;
        this.period = period;
        this.totalExpenses = totalExpenses;
        this.totalIncome = totalIncome;
    }

    public Ranking() {

    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getFamilyId() {
        return familyId;
    }

    public void setFamilyId(UUID familyId) {
        this.familyId = familyId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }
}

