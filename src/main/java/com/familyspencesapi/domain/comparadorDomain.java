package com.familyspencesapi.domain;

import java.util.UUID;

public class comparadorDomain {
    private UUID id;
    private String producto;
    private int precio;
    private String negocio;

    public comparadorDomain() {
        this.id = UUID.randomUUID();
    }

    public comparadorDomain(String producto, int precio, String negocio) {
        this.id = UUID.randomUUID();
        this.producto = producto;
        this.precio = precio;
        this.negocio = negocio;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProducto() {
        return producto;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public int getPrecio() {
        return precio;
    }

    public void setPrecio(int precio) {
        this.precio = precio;
    }

    public String getNegocio() {
        return negocio;
    }

    public void setNegocio(String negocio) {
        this.negocio = negocio;
    }
}

class BusquedaRequest {
    private String nombre;

    public BusquedaRequest() {}

    public BusquedaRequest(String nombre) {
        this.nombre = nombre;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}

class RespuestaProducto {
    private String mensaje;
    private UUID id;

    public RespuestaProducto() {}

    public RespuestaProducto(String mensaje, UUID id) {
        this.mensaje = mensaje;
        this.id = id;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}


