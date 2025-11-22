package com.familyspencesapi.domain.vacation;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "vacation")
public class Vacation {

    @Id
    @UuidGenerator
    @Column(name = "id_vacation", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "titulo_vacation", nullable = false)
    private String titulo;

    @Column(name = "descripcion_vacation", nullable = false, length = 2000)
    private String descripcion;

    @Column(name = "fecha_inicio_vacation", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin_vacation", nullable = false)
    private LocalDate fechaFin;

    @Column(name = "lugar_vacation", nullable = false)
    private String lugar;

    @Column(name = "presupuesto_vacation", nullable = false, precision = 15, scale = 2)
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