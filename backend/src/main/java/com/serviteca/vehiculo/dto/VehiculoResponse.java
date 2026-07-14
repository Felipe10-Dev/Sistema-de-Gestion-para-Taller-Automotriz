package com.serviteca.vehiculo.dto;

public class VehiculoResponse {

    private Long id;
    private String placa;
    private String marca;
    private String linea;
    private String modelo;
    private String color;
    private String cilindraje;
    private String tipoVehiculo;
    private String motor;
    private String combustible;
    private String vin;
    private Integer anio;
    private Long clienteId;
    private String clienteNombre;
    private boolean activo;

    public VehiculoResponse() {}

    public VehiculoResponse(Long id, String placa, String marca, String linea, String modelo,
                            String color, String cilindraje, String tipoVehiculo,
                            String motor, String combustible, String vin, Integer anio,
                            Long clienteId, String clienteNombre, boolean activo) {
        this.id = id;
        this.placa = placa;
        this.marca = marca;
        this.linea = linea;
        this.modelo = modelo;
        this.color = color;
        this.cilindraje = cilindraje;
        this.tipoVehiculo = tipoVehiculo;
        this.motor = motor;
        this.combustible = combustible;
        this.vin = vin;
        this.anio = anio;
        this.clienteId = clienteId;
        this.clienteNombre = clienteNombre;
        this.activo = activo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public String getClienteNombre() { return clienteNombre; }
    public void setClienteNombre(String clienteNombre) { this.clienteNombre = clienteNombre; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
