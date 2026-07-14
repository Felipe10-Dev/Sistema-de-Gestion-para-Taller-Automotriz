package com.serviteca.categoria.dto;

import jakarta.validation.constraints.NotBlank;

public class CategoriaRequest {

    @NotBlank(message = "El nombre de la categor\u00eda es obligatorio")
    private String nombre;

    private String descripcion;

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
}
