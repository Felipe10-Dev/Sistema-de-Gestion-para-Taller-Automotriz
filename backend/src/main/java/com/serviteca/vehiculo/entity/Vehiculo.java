package com.serviteca.vehiculo.entity;

import com.serviteca.cliente.entity.Cliente;
import com.serviteca.shared.util.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "vehiculos")
public class Vehiculo extends BaseEntity {

    @Column(name = "placa", unique = true, nullable = false, length = 10)
    private String placa;

    @Column(name = "marca", nullable = false, length = 50)
    private String marca;

    @Column(name = "linea", nullable = false, length = 50)
    private String linea;

    @Column(name = "modelo", nullable = false, length = 10)
    private String modelo;

    @Column(name = "color", length = 30)
    private String color;

    @Column(name = "cilindraje", length = 10)
    private String cilindraje;

    @Column(name = "tipo_vehiculo", length = 30)
    private String tipoVehiculo;

    @Column(name = "motor", length = 50)
    private String motor;

    @Column(name = "combustible", length = 30)
    private String combustible;

    @Column(name = "vin", length = 20)
    private String vin;

    @Column(name = "anio")
    private Integer anio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @PrePersist
    protected void onCreate() { com.serviteca.shared.util.TenantUtil.setTenantFields(this); }

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
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
}
