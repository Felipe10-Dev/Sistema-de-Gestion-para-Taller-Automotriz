package com.serviteca.proveedor.dto;

import jakarta.validation.constraints.NotBlank;

public class ProveedorRequest {

    @NotBlank(message = "El NIT es obligatorio")
    private String nit;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String contacto;
    private String telefono;
    private String email;
    private String direccion;

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
