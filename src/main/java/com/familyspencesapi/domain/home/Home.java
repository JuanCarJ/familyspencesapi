package com.familyspencesapi.domain.home;

import java.math.BigDecimal;
import java.util.List;

public class Home {
    private String userId;
    private BigDecimal balanceGeneral;
    private BigDecimal totalGastos;
    private BigDecimal ultimoIngreso;
    private BigDecimal totalIngresos;
    private List<Gasto> gastos;

    public Home() {}

    public Home(String userId, BigDecimal balanceGeneral, BigDecimal totalGastos,
                BigDecimal ultimoIngreso, BigDecimal totalIngresos, List<Gasto> gastos) {
        this.userId = userId;
        this.balanceGeneral = balanceGeneral;
        this.totalGastos = totalGastos;
        this.ultimoIngreso = ultimoIngreso;
        this.totalIngresos = totalIngresos;
        this.gastos = gastos;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getBalanceGeneral() { return balanceGeneral; }
    public void setBalanceGeneral(BigDecimal balanceGeneral) { this.balanceGeneral = balanceGeneral; }

    public BigDecimal getTotalGastos() { return totalGastos; }
    public void setTotalGastos(BigDecimal totalGastos) { this.totalGastos = totalGastos; }

    public BigDecimal getUltimoIngreso() { return ultimoIngreso; }
    public void setUltimoIngreso(BigDecimal ultimoIngreso) { this.ultimoIngreso = ultimoIngreso; }

    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }

    public List<Gasto> getGastos() { return gastos; }
    public void setGastos(List<Gasto> gastos) { this.gastos = gastos; }
}
