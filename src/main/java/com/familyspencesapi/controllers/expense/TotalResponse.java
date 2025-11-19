package com.familyspencesapi.controllers.expense;

import java.math.BigDecimal;

public class TotalResponse {

    private String period;
    private BigDecimal total;
    private String formattedTotal;

    public TotalResponse(String period, BigDecimal total) {
        this.period = period;
        this.total = total;
        this.formattedTotal = String.format("$%.2f", total);
    }

    public String getPeriod() { return period; }
    public void setPeriod(String period) { this.period = period; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getFormattedTotal() { return formattedTotal; }
    public void setFormattedTotal(String formattedTotal) { this.formattedTotal = formattedTotal; }

}
