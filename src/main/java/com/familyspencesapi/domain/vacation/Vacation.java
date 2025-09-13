package com.familyspencesapi.domain.vacation;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vacation")
public class Vacation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idVacation", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "tituloVacation", nullable = false)
    private String titulo;

    @Column(name = "descripcionVacation", nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "fechaInicioVacation", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fechaFinVacation", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "lugarVacation", nullable = false)
    private String lugar;

    @Column(name = "presupuestoVacation", nullable = false, precision = 15, scale = 2)
    private BigDecimal presupuesto;

    public Vacation() {}

    public Vacation(UUID id) {
        this.id = id;
    }

    public Vacation(String titulo, String descripcion, LocalDate fechaInicio,
                    LocalDate fechaFin, String lugar, BigDecimal presupuesto) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.lugar = lugar;
        this.presupuesto = presupuesto;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getLugar() {
        return lugar;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }
}
