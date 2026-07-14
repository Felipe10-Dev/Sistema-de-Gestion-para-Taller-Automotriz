package com.serviteca.crm.dto;

import java.time.LocalDateTime;

public class ClienteInactivoResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private LocalDateTime ultimaVisita;
    private long mesesSinVisita;
    private long totalOrdenes;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public LocalDateTime getUltimaVisita() { return ultimaVisita; }
    public void setUltimaVisita(LocalDateTime ultimaVisita) { this.ultimaVisita = ultimaVisita; }
    public long getMesesSinVisita() { return mesesSinVisita; }
    public void setMesesSinVisita(long mesesSinVisita) { this.mesesSinVisita = mesesSinVisita; }
    public long getTotalOrdenes() { return totalOrdenes; }
    public void setTotalOrdenes(long totalOrdenes) { this.totalOrdenes = totalOrdenes; }
}
