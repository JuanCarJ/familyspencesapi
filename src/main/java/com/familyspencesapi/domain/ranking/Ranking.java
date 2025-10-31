package com.familyspencesapi.domain.ranking;

import com.familyspencesapi.domain.users.RegisterUser;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private RegisterUser user;

    @Column(nullable = false, length = 7)
    private String period;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalExpenses;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalIncome;

    public Ranking() {
    }


    public Ranking(UUID familyId, RegisterUser user, String period, BigDecimal totalExpenses, BigDecimal totalIncome) {
        this.id = id;
        this.familyId = familyId;
        this.user = user;
        this.period = period;
        this.totalExpenses = totalExpenses;
        this.totalIncome = totalIncome;
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

    public RegisterUser getUser() {
        return user;
    }

    public void setUser(RegisterUser user) {
        this.user = user;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }
}

