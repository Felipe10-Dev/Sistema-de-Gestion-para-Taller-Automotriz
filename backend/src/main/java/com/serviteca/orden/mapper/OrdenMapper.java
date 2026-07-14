package com.serviteca.orden.mapper;

import com.serviteca.orden.dto.OrdenTrabajoResponse;
import com.serviteca.orden.entity.HistorialEstado;
import com.serviteca.orden.entity.OrdenObservacion;
import com.serviteca.orden.entity.OrdenProducto;
import com.serviteca.orden.entity.OrdenServicio;
import com.serviteca.orden.entity.OrdenTrabajo;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrdenMapper {

    public OrdenTrabajoResponse toResponse(OrdenTrabajo orden) {
        return toResponse(orden, null, null, null, null);
    }

    public OrdenTrabajoResponse toResponse(OrdenTrabajo orden,
                                            List<OrdenServicio> servicios,
                                            List<OrdenProducto> productos) {
        return toResponse(orden, servicios, productos, null, null);
    }

    public OrdenTrabajoResponse toResponse(OrdenTrabajo orden,
                                            List<OrdenServicio> servicios,
                                            List<OrdenProducto> productos,
                                            List<HistorialEstado> historial,
                                            List<OrdenObservacion> observaciones) {
        OrdenTrabajoResponse response = new OrdenTrabajoResponse();
        response.setId(orden.getId());
        response.setNumeroOrden(orden.getNumeroOrden());
        response.setClienteId(orden.getCliente().getId());
        response.setClienteNombre(orden.getCliente().getNombre() + " " + orden.getCliente().getApellido());
        response.setClienteDocumento(orden.getCliente().getNumeroDocumento());
        response.setVehiculoId(orden.getVehiculo().getId());
        response.setVehiculoPlaca(orden.getVehiculo().getPlaca());
        response.setVehiculoMarca(orden.getVehiculo().getMarca());
        response.setVehiculoLinea(orden.getVehiculo().getLinea());
        response.setKilometraje(orden.getKilometraje());
        response.setEstado(orden.getEstado());
        response.setFechaIngreso(orden.getFechaIngreso());
        response.setFechaSalida(orden.getFechaSalida());
        response.setTecnicoId(orden.getTecnico() != null ? orden.getTecnico().getId() : null);
        response.setTecnicoNombre(orden.getTecnico() != null ?
                orden.getTecnico().getNombre() + " " + orden.getTecnico().getApellido() : null);
        response.setObservaciones(orden.getObservaciones());
        response.setEstadoFinanciero(orden.getEstadoFinanciero());
        response.setTotalServicios(orden.getTotalServicios());
        response.setTotalProductos(orden.getTotalProductos());
        response.setTotalGeneral(orden.getTotalGeneral());

        if (servicios != null) {
            response.setServicios(servicios.stream().map(this::toServicioResponse).toList());
        }
        if (productos != null) {
            response.setProductos(productos.stream().map(this::toProductoResponse).toList());
        }
        if (historial != null) {
            response.setHistorial(historial.stream().map(this::toHistorialResponse).toList());
        }
        if (observaciones != null) {
            response.setObservacionesList(observaciones.stream().map(this::toObservacionResponse).toList());
        }

        return response;
    }

    private OrdenTrabajoResponse.ServicioResponse toServicioResponse(OrdenServicio os) {
        OrdenTrabajoResponse.ServicioResponse sr = new OrdenTrabajoResponse.ServicioResponse();
        sr.setId(os.getId());
        sr.setServicioId(os.getServicio().getId());
        sr.setServicioNombre(os.getServicio().getNombre());
        sr.setCantidad(os.getCantidad());
        sr.setPrecioUnitario(os.getPrecioUnitario());
        sr.setSubtotal(os.getSubtotal());
        sr.setObservaciones(os.getObservaciones());
        return sr;
    }

    private OrdenTrabajoResponse.ProductoResponse toProductoResponse(OrdenProducto op) {
        OrdenTrabajoResponse.ProductoResponse pr = new OrdenTrabajoResponse.ProductoResponse();
        pr.setId(op.getId());
        pr.setProductoId(op.getProducto().getId());
        pr.setProductoNombre(op.getProducto().getNombre());
        pr.setProductoCodigo(op.getProducto().getCodigo());
        pr.setCantidad(op.getCantidad());
        pr.setPrecioUnitario(op.getPrecioUnitario());
        pr.setSubtotal(op.getSubtotal());
        return pr;
    }

    private OrdenTrabajoResponse.HistorialResponse toHistorialResponse(HistorialEstado h) {
        OrdenTrabajoResponse.HistorialResponse hr = new OrdenTrabajoResponse.HistorialResponse();
        hr.setId(h.getId());
        hr.setEstadoAnterior(h.getEstadoAnterior());
        hr.setEstadoNuevo(h.getEstadoNuevo());
        hr.setUsuario(h.getUsuario());
        hr.setFecha(h.getFecha());
        hr.setObservacion(h.getObservacion());
        return hr;
    }

    private OrdenTrabajoResponse.ObservacionResponse toObservacionResponse(OrdenObservacion o) {
        OrdenTrabajoResponse.ObservacionResponse or = new OrdenTrabajoResponse.ObservacionResponse();
        or.setId(o.getId());
        or.setUsuario(o.getUsuario());
        or.setFecha(o.getFecha());
        or.setComentario(o.getComentario());
        return or;
    }
}
