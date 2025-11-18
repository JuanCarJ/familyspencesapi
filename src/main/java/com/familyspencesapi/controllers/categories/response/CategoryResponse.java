package com.familyspencesapi.controllers.categories.response;

import com.familyspencesapi.domain.categories.Category;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoryResponse {
    private String mensaje;
    private Category categoria;

    public CategoryResponse(String mensaje, Category categoria) {
        this.mensaje = mensaje;
        this.categoria = categoria;
    }

    public CategoryResponse(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public Category getCategoria() {
        return categoria;
    }

    public void setCategoria(Category categoria) {
        this.categoria = categoria;
    }
}
