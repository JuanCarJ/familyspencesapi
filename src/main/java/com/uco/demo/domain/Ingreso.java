package com.uco.demo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "ingresos")
public class Ingreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idIngreso;

    private Long idFamily;
    private String responsable;
    private String titulo;
    private String descripcion;
    private String periodo;
    private Double total;

    public Ingreso() {}

    public Ingreso(Long idFamily, String responsable, String titulo, String descripcion, String periodo, Double total) {
        this.idFamily = idFamily;
        this.responsable = responsable;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.periodo = periodo;
        this.total = total;
    }

    // Getters y Setters
    public Long getIdIngreso() { return idIngreso; }
    public void setIdIngreso(Long idIngreso) { this.idIngreso = idIngreso; }

    public Long getIdFamily() { return idFamily; }
    public void setIdFamily(Long idFamily) { this.idFamily = idFamily; }

    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getPeriodo() { return periodo; }
    public void setPeriodo(String periodo) { this.periodo = periodo; }

    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
}
