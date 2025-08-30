package com.familyspencesapi.domain.familia;

import java.util.UUID;

public class Familiar {

    private UUID idFamily;            // Para identificar a qué familia pertenece
    private String nombreCompleto;
    private String fechaNacimiento;
    private String tipoDocumento;
    private String documento;
    private String email;
    private String parentesco;
    private String celular;
    private String direccion;
    private String password;
    private String repetirPassword;

    // Constructor vacío (Spring necesita esto)
    public Familiar() {}

    // Constructor con parámetros
    public Familiar(UUID idFamily, String nombreCompleto, String fechaNacimiento, String tipoDocumento,
                    String documento, String email, String parentesco, String celular,
                    String direccion, String password, String repetirPassword) {
        this.idFamily = idFamily;
        this.nombreCompleto = nombreCompleto;
        this.fechaNacimiento = fechaNacimiento;
        this.tipoDocumento = tipoDocumento;
        this.documento = documento;
        this.email = email;
        this.parentesco = parentesco;
        this.celular = celular;
        this.direccion = direccion;
        this.password = password;
        this.repetirPassword = repetirPassword;
    }

    // Getters y Setters
    public UUID getIdFamily() {
        return idFamily;
    }

    public void setIdFamily(UUID idFamily) {
        this.idFamily = idFamily;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRepetirPassword() {
        return repetirPassword;
    }

    public void setRepetirPassword(String repetirPassword) {
        this.repetirPassword = repetirPassword;
    }
}
