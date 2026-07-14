package com.serviteca.proveedor.entity;

import com.serviteca.shared.util.BaseEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "proveedores")
public class Proveedor extends BaseEntity {

    @Column(name = "nit", unique = true, nullable = false, length = 20)
    private String nit;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Column(name = "contacto", length = 100)
    private String contacto;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "direccion", length = 200)
    private String direccion;

    @PrePersist
    protected void onCreate() { com.serviteca.shared.util.TenantUtil.setTenantFields(this); }

    public String getNit() { return nit; }
    public void setNit(String nit) { this.nit = nit; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getContacto() { return contacto; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
}
