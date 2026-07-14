package com.serviteca.dashboard.dto;

public class DashboardResponse {

    private long totalClientes;
    private long totalVehiculos;
    private long ordenesPendientes;
    private long ordenesEnDiagnostico;
    private long ordenesEnProceso;
    private long ordenesEsperandoRepuestos;
    private long ordenesListas;
    private long ordenesEntregadasHoy;
    private long productosBajoStock;
    private long totalUsuarios;
    private double ingresosHoy;

    private boolean cajaAbierta;
    private String cajaUsuario;
    private long ordenesSinPagar;
    private long ordenesParciales;
    private long ordenesPagadas;
    private double saldoPendienteTotal;

    private long vehiculosProximosMantenimiento;
    private long vehiculosSinVisitas12Meses;
    private long clientesFrecuentes;
    private long vehiculosMayorInversion;

    private long productosBajoMinimo;
    private long productosAgotados;
    private double valorTotalInventario;
    private double comprasDelMes;
    private long proveedoresMasUtilizados;

    public long getTotalClientes() { return totalClientes; }
    public void setTotalClientes(long totalClientes) { this.totalClientes = totalClientes; }
    public long getTotalVehiculos() { return totalVehiculos; }
    public void setTotalVehiculos(long totalVehiculos) { this.totalVehiculos = totalVehiculos; }
    public long getOrdenesPendientes() { return ordenesPendientes; }
    public void setOrdenesPendientes(long ordenesPendientes) { this.ordenesPendientes = ordenesPendientes; }
    public long getOrdenesEnDiagnostico() { return ordenesEnDiagnostico; }
    public void setOrdenesEnDiagnostico(long ordenesEnDiagnostico) { this.ordenesEnDiagnostico = ordenesEnDiagnostico; }
    public long getOrdenesEnProceso() { return ordenesEnProceso; }
    public void setOrdenesEnProceso(long ordenesEnProceso) { this.ordenesEnProceso = ordenesEnProceso; }
    public long getOrdenesEsperandoRepuestos() { return ordenesEsperandoRepuestos; }
    public void setOrdenesEsperandoRepuestos(long ordenesEsperandoRepuestos) { this.ordenesEsperandoRepuestos = ordenesEsperandoRepuestos; }
    public long getOrdenesListas() { return ordenesListas; }
    public void setOrdenesListas(long ordenesListas) { this.ordenesListas = ordenesListas; }
    public long getOrdenesEntregadasHoy() { return ordenesEntregadasHoy; }
    public void setOrdenesEntregadasHoy(long ordenesEntregadasHoy) { this.ordenesEntregadasHoy = ordenesEntregadasHoy; }
    public long getProductosBajoStock() { return productosBajoStock; }
    public void setProductosBajoStock(long productosBajoStock) { this.productosBajoStock = productosBajoStock; }
    public long getTotalUsuarios() { return totalUsuarios; }
    public void setTotalUsuarios(long totalUsuarios) { this.totalUsuarios = totalUsuarios; }
    public double getIngresosHoy() { return ingresosHoy; }
    public void setIngresosHoy(double ingresosHoy) { this.ingresosHoy = ingresosHoy; }

    public boolean isCajaAbierta() { return cajaAbierta; }
    public void setCajaAbierta(boolean cajaAbierta) { this.cajaAbierta = cajaAbierta; }
    public String getCajaUsuario() { return cajaUsuario; }
    public void setCajaUsuario(String cajaUsuario) { this.cajaUsuario = cajaUsuario; }
    public long getOrdenesSinPagar() { return ordenesSinPagar; }
    public void setOrdenesSinPagar(long ordenesSinPagar) { this.ordenesSinPagar = ordenesSinPagar; }
    public long getOrdenesParciales() { return ordenesParciales; }
    public void setOrdenesParciales(long ordenesParciales) { this.ordenesParciales = ordenesParciales; }
    public long getOrdenesPagadas() { return ordenesPagadas; }
    public void setOrdenesPagadas(long ordenesPagadas) { this.ordenesPagadas = ordenesPagadas; }
    public double getSaldoPendienteTotal() { return saldoPendienteTotal; }
    public void setSaldoPendienteTotal(double saldoPendienteTotal) { this.saldoPendienteTotal = saldoPendienteTotal; }

    public long getVehiculosProximosMantenimiento() { return vehiculosProximosMantenimiento; }
    public void setVehiculosProximosMantenimiento(long v) { this.vehiculosProximosMantenimiento = v; }
    public long getVehiculosSinVisitas12Meses() { return vehiculosSinVisitas12Meses; }
    public void setVehiculosSinVisitas12Meses(long v) { this.vehiculosSinVisitas12Meses = v; }
    public long getClientesFrecuentes() { return clientesFrecuentes; }
    public void setClientesFrecuentes(long v) { this.clientesFrecuentes = v; }
    public long getVehiculosMayorInversion() { return vehiculosMayorInversion; }
    public void setVehiculosMayorInversion(long v) { this.vehiculosMayorInversion = v; }

    public long getProductosBajoMinimo() { return productosBajoMinimo; }
    public void setProductosBajoMinimo(long v) { this.productosBajoMinimo = v; }
    public long getProductosAgotados() { return productosAgotados; }
    public void setProductosAgotados(long v) { this.productosAgotados = v; }
    public double getValorTotalInventario() { return valorTotalInventario; }
    public void setValorTotalInventario(double v) { this.valorTotalInventario = v; }
    public double getComprasDelMes() { return comprasDelMes; }
    public void setComprasDelMes(double v) { this.comprasDelMes = v; }
    public long getProveedoresMasUtilizados() { return proveedoresMasUtilizados; }
    public void setProveedoresMasUtilizados(long v) { this.proveedoresMasUtilizados = v; }
}
