package com.serviteca.vehiculo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class VehiculoRequest {

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @NotBlank(message = "La marca es obligatoria")
    private String marca;

    @NotBlank(message = "La l\u00ednea es obligatoria")
    private String linea;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    private String color;
    private String cilindraje;
    private String tipoVehiculo;
    private String motor;
    private String combustible;
    private String vin;
    private Integer anio;

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getLinea() { return linea; }
    public void setLinea(String linea) { this.linea = linea; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getCilindraje() { return cilindraje; }
    public void setCilindraje(String cilindraje) { this.cilindraje = cilindraje; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }
    public String getMotor() { return motor; }
    public void setMotor(String motor) { this.motor = motor; }
    public String getCombustible() { return combustible; }
    public void setCombustible(String combustible) { this.combustible = combustible; }
    public String getVin() { return vin; }
    public void setVin(String vin) { this.vin = vin; }
    public Integer getAnio() { return anio; }
    public void setAnio(Integer anio) { this.anio = anio; }
    public Long getClienteId() { return clienteId; }
    public void setClienteId(Long clienteId) { this.clienteId = clienteId; }
}
