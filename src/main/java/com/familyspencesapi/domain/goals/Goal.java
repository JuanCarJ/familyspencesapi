package com.familyspencesapi.domain.goals;

import com.familyspencesapi.domain.categories.Category;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "goal")
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "idGoal", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "nameGoal", nullable = false)
    private String nombre;
    @Column(name = "descripcionGoal", nullable = false)
    private String descripcion;

    //@ManyToOne
    //@JoinColumn(name = "id", nullable = false)
    //private Category categoria;

    @Column(name = "topeGoal", nullable = false)
    private double tope;
    @Column(name = "fechaLimiteGoal", nullable = false)
    private LocalDateTime fechaLimite;
    @Column(name = "metaDiariaGoal", nullable = false)
    private double metaDiaria;


   public Goal() {}

    //constructor
    public Goal(UUID id) {
        this.id = id;
    }

    //constructor con parametros

    public Goal(String nombre, String descripcion, Category categoria, double tope, LocalDateTime fechaLimite, double metaDiaria, UUID id) {
        this.nombre = nombre;
        this.descripcion = descripcion;
       // this.categoria = categoria;
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

    /*public Category getCategoria() {
        return categoria;
    }*/

    /*public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }*/

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
