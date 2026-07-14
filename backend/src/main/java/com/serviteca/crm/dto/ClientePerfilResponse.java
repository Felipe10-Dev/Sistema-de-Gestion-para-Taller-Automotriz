package com.serviteca.crm.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ClientePerfilResponse {

    private Long id;
    private String tipoDocumento;
    private String numeroDocumento;
    private String nombre;
    private String apellido;
    private String telefono;
    private String email;
    private String direccion;
    private boolean activo;
    private String clasificacion;
    private LocalDateTime fechaRegistro;
    private LocalDateTime primeraVisita;
    private LocalDateTime ultimaVisita;
    private long totalOrdenes;
    private long totalVehiculos;
    private BigDecimal totalGastado;
    private BigDecimal promedioPorOrden;
    private List<ClienteNotaResponse> notas;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoDocumento() { return tipoDocumento; }
    public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
    public String getNumeroDocumento() { return numeroDocumento; }
    public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public String getClasificacion() { return clasificacion; }
    public void setClasificacion(String clasificacion) { this.clasificacion = clasificacion; }
    public LocalDateTime getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(LocalDateTime fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public LocalDateTime getPrimeraVisita() { return primeraVisita; }
    public void setPrimeraVisita(LocalDateTime primeraVisita) { this.primeraVisita = primeraVisita; }
    public LocalDateTime getUltimaVisita() { return ultimaVisita; }
    public void setUltimaVisita(LocalDateTime ultimaVisita) { this.ultimaVisita = ultimaVisita; }
    public long getTotalOrdenes() { return totalOrdenes; }
    public void setTotalOrdenes(long totalOrdenes) { this.totalOrdenes = totalOrdenes; }
    public long getTotalVehiculos() { return totalVehiculos; }
    public void setTotalVehiculos(long totalVehiculos) { this.totalVehiculos = totalVehiculos; }
    public BigDecimal getTotalGastado() { return totalGastado; }
    public void setTotalGastado(BigDecimal totalGastado) { this.totalGastado = totalGastado; }
    public BigDecimal getPromedioPorOrden() { return promedioPorOrden; }
    public void setPromedioPorOrden(BigDecimal promedioPorOrden) { this.promedioPorOrden = promedioPorOrden; }
    public List<ClienteNotaResponse> getNotas() { return notas; }
    public void setNotas(List<ClienteNotaResponse> notas) { this.notas = notas; }
}
