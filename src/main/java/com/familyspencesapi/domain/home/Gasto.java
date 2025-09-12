package com.familyspencesapi.domain.home;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Gasto {
    private String descripcion;
    private BigDecimal monto;
    private LocalDateTime fecha;

    public Gasto() {}

    public Gasto(String descripcion, BigDecimal monto, LocalDateTime fecha) {
        this.descripcion = descripcion;
        this.monto = monto;
        this.fecha = fecha;
    }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public BigDecimal getMonto() { return monto; }
    public void setMonto(BigDecimal monto) { this.monto = monto; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
