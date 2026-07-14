package com.serviteca.orden.dto;

import jakarta.validation.constraints.NotBlank;

public class ObservacionRequest {

    @NotBlank(message = "El comentario es obligatorio")
    private String comentario;

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
