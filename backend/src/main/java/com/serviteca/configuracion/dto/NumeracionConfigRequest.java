package com.serviteca.configuracion.dto;

public class NumeracionConfigRequest {
    private String modulo;
    private String prefijo;
    private String sufijo;
    private int longitud;
    private int numeroInicial;
    private boolean reinicioAnual;
    private Boolean activo;

    public String getModulo() { return modulo; }
    public void setModulo(String modulo) { this.modulo = modulo; }
    public String getPrefijo() { return prefijo; }
    public void setPrefijo(String prefijo) { this.prefijo = prefijo; }
    public String getSufijo() { return sufijo; }
    public void setSufijo(String sufijo) { this.sufijo = sufijo; }
    public int getLongitud() { return longitud; }
    public void setLongitud(int longitud) { this.longitud = longitud; }
    public int getNumeroInicial() { return numeroInicial; }
    public void setNumeroInicial(int numeroInicial) { this.numeroInicial = numeroInicial; }
    public boolean isReinicioAnual() { return reinicioAnual; }
    public void setReinicioAnual(boolean reinicioAnual) { this.reinicioAnual = reinicioAnual; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
