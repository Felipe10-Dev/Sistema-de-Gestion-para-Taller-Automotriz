package com.serviteca.crm.dto;

import java.util.List;

public class CrmDashboardResponse {

    private long clientesNuevosEsteMes;
    private long clientesActivos;
    private long clientesFrecuentes;
    private long clientesVip;
    private long clientesInactivos12Meses;
    private long clientesConMantenimientoProximo;
    private List<RankingItem> top10ClientesFacturacion;
    private List<RankingItem> top10ClientesVisitas;

    public long getClientesNuevosEsteMes() { return clientesNuevosEsteMes; }
    public void setClientesNuevosEsteMes(long clientesNuevosEsteMes) { this.clientesNuevosEsteMes = clientesNuevosEsteMes; }
    public long getClientesActivos() { return clientesActivos; }
    public void setClientesActivos(long clientesActivos) { this.clientesActivos = clientesActivos; }
    public long getClientesFrecuentes() { return clientesFrecuentes; }
    public void setClientesFrecuentes(long clientesFrecuentes) { this.clientesFrecuentes = clientesFrecuentes; }
    public long getClientesVip() { return clientesVip; }
    public void setClientesVip(long clientesVip) { this.clientesVip = clientesVip; }
    public long getClientesInactivos12Meses() { return clientesInactivos12Meses; }
    public void setClientesInactivos12Meses(long clientesInactivos12Meses) { this.clientesInactivos12Meses = clientesInactivos12Meses; }
    public long getClientesConMantenimientoProximo() { return clientesConMantenimientoProximo; }
    public void setClientesConMantenimientoProximo(long clientesConMantenimientoProximo) { this.clientesConMantenimientoProximo = clientesConMantenimientoProximo; }
    public List<RankingItem> getTop10ClientesFacturacion() { return top10ClientesFacturacion; }
    public void setTop10ClientesFacturacion(List<RankingItem> top10ClientesFacturacion) { this.top10ClientesFacturacion = top10ClientesFacturacion; }
    public List<RankingItem> getTop10ClientesVisitas() { return top10ClientesVisitas; }
    public void setTop10ClientesVisitas(List<RankingItem> top10ClientesVisitas) { this.top10ClientesVisitas = top10ClientesVisitas; }
}
