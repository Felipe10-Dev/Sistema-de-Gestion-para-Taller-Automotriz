package com.serviteca.crm.dto;

import java.time.LocalDateTime;

public class ClienteNotaResponse {

    private Long id;
    private String usuario;
    private LocalDateTime fecha;
    private String comentario;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}
