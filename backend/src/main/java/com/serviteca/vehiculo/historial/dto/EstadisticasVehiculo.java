package com.serviteca.vehiculo.historial.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class EstadisticasVehiculo {

    private long totalVisitas;
    private BigDecimal totalInvertido;
    private LocalDateTime ultimaVisita;
    private double promedioDiasEntreVisitas;
    private String servicioMasFrecuente;
    private String productoMasUtilizado;

    public long getTotalVisitas() { return totalVisitas; }
    public void setTotalVisitas(long totalVisitas) { this.totalVisitas = totalVisitas; }
    public BigDecimal getTotalInvertido() { return totalInvertido; }
    public void setTotalInvertido(BigDecimal totalInvertido) { this.totalInvertido = totalInvertido; }
    public LocalDateTime getUltimaVisita() { return ultimaVisita; }
    public void setUltimaVisita(LocalDateTime ultimaVisita) { this.ultimaVisita = ultimaVisita; }
    public double getPromedioDiasEntreVisitas() { return promedioDiasEntreVisitas; }
    public void setPromedioDiasEntreVisitas(double promedioDiasEntreVisitas) { this.promedioDiasEntreVisitas = promedioDiasEntreVisitas; }
    public String getServicioMasFrecuente() { return servicioMasFrecuente; }
    public void setServicioMasFrecuente(String servicioMasFrecuente) { this.servicioMasFrecuente = servicioMasFrecuente; }
    public String getProductoMasUtilizado() { return productoMasUtilizado; }
    public void setProductoMasUtilizado(String productoMasUtilizado) { this.productoMasUtilizado = productoMasUtilizado; }
}
