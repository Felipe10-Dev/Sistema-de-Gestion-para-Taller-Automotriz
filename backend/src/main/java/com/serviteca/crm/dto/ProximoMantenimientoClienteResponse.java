package com.serviteca.crm.dto;

import com.serviteca.vehiculo.mantenimiento.dto.ProximoMantenimientoResponse;

import java.util.List;

public class ProximoMantenimientoClienteResponse {

    private Long vehiculoId;
    private String vehiculoPlaca;
    private String vehiculoMarca;
    private String vehiculoLinea;
    private List<ProximoMantenimientoResponse> mantenimientos;

    public Long getVehiculoId() { return vehiculoId; }
    public void setVehiculoId(Long vehiculoId) { this.vehiculoId = vehiculoId; }
    public String getVehiculoPlaca() { return vehiculoPlaca; }
    public void setVehiculoPlaca(String vehiculoPlaca) { this.vehiculoPlaca = vehiculoPlaca; }
    public String getVehiculoMarca() { return vehiculoMarca; }
    public void setVehiculoMarca(String vehiculoMarca) { this.vehiculoMarca = vehiculoMarca; }
    public String getVehiculoLinea() { return vehiculoLinea; }
    public void setVehiculoLinea(String vehiculoLinea) { this.vehiculoLinea = vehiculoLinea; }
    public List<ProximoMantenimientoResponse> getMantenimientos() { return mantenimientos; }
    public void setMantenimientos(List<ProximoMantenimientoResponse> mantenimientos) { this.mantenimientos = mantenimientos; }
}
