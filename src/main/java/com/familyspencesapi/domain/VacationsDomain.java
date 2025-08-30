package com.example.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Vacation {

    // Atributos privados
    private Long id;
    private String titulo;
    private String descripcion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String lugar;
    private BigDecimal presupuesto;

    // Constructor por defecto
    public Vacation() {
    }

    // Constructor con parámetros básicos
    public Vacation(String titulo, LocalDate fechaInicio, LocalDate fechaFin, String lugar) {
        this.titulo = titulo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.lugar = lugar;
    }

    // Constructor completo
    public Vacation(Long id, String titulo, String descripcion, LocalDate fechaInicio,
                    LocalDate fechaFin, String lugar, BigDecimal presupuesto) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.lugar = lugar;
        this.presupuesto = presupuesto;
    }

    // Constructor completo sin ID (para nuevas vacaciones)
    public Vacation(String titulo, String descripcion, LocalDate fechaInicio,
                    LocalDate fechaFin, String lugar, BigDecimal presupuesto) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.lugar = lugar;
        this.presupuesto = presupuesto;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getLugar() {
        return lugar;
    }

    public BigDecimal getPresupuesto() {
        return presupuesto;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setLugar(String lugar) {
        this.lugar = lugar;
    }

    public void setPresupuesto(BigDecimal presupuesto) {
        this.presupuesto = presupuesto;
    }

}