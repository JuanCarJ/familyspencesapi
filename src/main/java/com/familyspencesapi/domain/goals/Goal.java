package com.familyspencesapi.domain.goals;

import java.time.LocalDateTime;
import java.util.UUID;

public class Goal {

    private UUID id;
    private String nombre;
    private String descripcion;
    private UUID idCategoria;
    private double tope;
    private LocalDateTime fechaLimite;
    private double metaDiaria;


    //constructor
    public Goal(UUID id) {
        this.id = id;
    }

    //constructor con parametros

    public Goal(String nombre, String descripcion, UUID idCategoria, double tope, LocalDateTime fechaLimite, double metaDiaria, UUID id) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.idCategoria = idCategoria;
        this.tope = tope;
        this.fechaLimite = fechaLimite;
        this.metaDiaria = metaDiaria;
        this.id = id;
    }


    //getters and setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public UUID getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(UUID idCategoria) {
        this.idCategoria = idCategoria;
    }

    public double getTope() {
        return tope;
    }

    public void setTope(double tope) {
        this.tope = tope;
    }

    public LocalDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(LocalDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public double getMetaDiaria() {
        return metaDiaria;
    }

    public void setMetaDiaria(double metaDiaria) {
        this.metaDiaria = metaDiaria;
    }
}
