package com.serviteca.multiempresa.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardConsolidadoResponse {
    private long totalClientes;
    private long totalVehiculos;
    private long ordenesActivas;
    private long ordenesPendientesPago;
    private BigDecimal totalIngresos;
    private long totalProductos;
    private long totalProveedores;
    private Map<String, Long> ordenesPorEstado;
    private Map<String, BigDecimal> ingresosPorSede;

    public long getTotalClientes() { return totalClientes; }
    public void setTotalClientes(long totalClientes) { this.totalClientes = totalClientes; }
    public long getTotalVehiculos() { return totalVehiculos; }
    public void setTotalVehiculos(long totalVehiculos) { this.totalVehiculos = totalVehiculos; }
    public long getOrdenesActivas() { return ordenesActivas; }
    public void setOrdenesActivas(long ordenesActivas) { this.ordenesActivas = ordenesActivas; }
    public long getOrdenesPendientesPago() { return ordenesPendientesPago; }
    public void setOrdenesPendientesPago(long ordenesPendientesPago) { this.ordenesPendientesPago = ordenesPendientesPago; }
    public BigDecimal getTotalIngresos() { return totalIngresos; }
    public void setTotalIngresos(BigDecimal totalIngresos) { this.totalIngresos = totalIngresos; }
    public long getTotalProductos() { return totalProductos; }
    public void setTotalProductos(long totalProductos) { this.totalProductos = totalProductos; }
    public long getTotalProveedores() { return totalProveedores; }
    public void setTotalProveedores(long totalProveedores) { this.totalProveedores = totalProveedores; }
    public Map<String, Long> getOrdenesPorEstado() { return ordenesPorEstado; }
    public void setOrdenesPorEstado(Map<String, Long> ordenesPorEstado) { this.ordenesPorEstado = ordenesPorEstado; }
    public Map<String, BigDecimal> getIngresosPorSede() { return ingresosPorSede; }
    public void setIngresosPorSede(Map<String, BigDecimal> ingresosPorSede) { this.ingresosPorSede = ingresosPorSede; }
}
