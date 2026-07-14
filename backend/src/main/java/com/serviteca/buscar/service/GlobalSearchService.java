package com.serviteca.buscar.service;

import com.serviteca.buscar.dto.GlobalSearchResult;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.orden.repository.OrdenTrabajoRepository;
import com.serviteca.producto.repository.ProductoRepository;
import com.serviteca.servicio.repository.ServicioRepository;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GlobalSearchService {

    private final ClienteRepository clienteRepository;
    private final VehiculoRepository vehiculoRepository;
    private final OrdenTrabajoRepository ordenTrabajoRepository;
    private final ProductoRepository productoRepository;
    private final ServicioRepository servicioRepository;

    public GlobalSearchService(ClienteRepository clienteRepository,
                                VehiculoRepository vehiculoRepository,
                                OrdenTrabajoRepository ordenTrabajoRepository,
                                ProductoRepository productoRepository,
                                ServicioRepository servicioRepository) {
        this.clienteRepository = clienteRepository;
        this.vehiculoRepository = vehiculoRepository;
        this.ordenTrabajoRepository = ordenTrabajoRepository;
        this.productoRepository = productoRepository;
        this.servicioRepository = servicioRepository;
    }

    public Page<GlobalSearchResult> search(String q, String tipo, int page, int size) {
        if (q == null || q.isBlank()) return Page.empty();
        Pageable pageable = PageRequest.of(page, size);
        List<GlobalSearchResult> results = new ArrayList<>();
        long total = 0;

        boolean all = "all".equalsIgnoreCase(tipo);

        if (all || "clientes".equalsIgnoreCase(tipo)) {
            var clientes = clienteRepository.search(q, pageable);
            total += clientes.getTotalElements();
            clientes.forEach(c -> results.add(new GlobalSearchResult(
                "CLIENTE", c.getId(),
                c.getNombre() + " " + c.getApellido(),
                c.getNumeroDocumento() + " | " + (c.getTelefono() != null ? c.getTelefono() : ""),
                "/clientes"
            )));
        }

        if (all || "vehiculos".equalsIgnoreCase(tipo)) {
            var vehiculos = vehiculoRepository.search(q, pageable);
            total += vehiculos.getTotalElements();
            vehiculos.forEach(v -> results.add(new GlobalSearchResult(
                "VEHICULO", v.getId(),
                v.getPlaca() + " - " + v.getMarca() + " " + v.getLinea(),
                v.getVin() != null ? v.getVin() : v.getModelo(),
                "/vehiculos"
            )));
        }

        if (all || "ordenes".equalsIgnoreCase(tipo)) {
            var ordenes = ordenTrabajoRepository.search(q, pageable);
            total += ordenes.getTotalElements();
            ordenes.forEach(o -> results.add(new GlobalSearchResult(
                "ORDEN", o.getId(),
                "Orden #" + o.getNumeroOrden(),
                o.getCliente().getNombre() + " " + o.getCliente().getApellido() + " | " + o.getVehiculo().getPlaca(),
                "/ordenes"
            )));
        }

        if (all || "productos".equalsIgnoreCase(tipo)) {
            var productos = productoRepository.search(q, pageable);
            total += productos.getTotalElements();
            productos.forEach(p -> results.add(new GlobalSearchResult(
                "PRODUCTO", p.getId(),
                p.getNombre(),
                p.getCodigo() + " | $" + (p.getPrecioVenta() != null ? p.getPrecioVenta() : 0),
                "/productos"
            )));
        }

        if (all || "servicios".equalsIgnoreCase(tipo)) {
            var servicios = servicioRepository.search(q, pageable);
            total += servicios.getTotalElements();
            servicios.forEach(s -> results.add(new GlobalSearchResult(
                "SERVICIO", s.getId(),
                s.getNombre(),
                "$" + (s.getPrecio() != null ? s.getPrecio() : 0),
                "/servicios"
            )));
        }

        return new PageImpl<>(results, pageable, total);
    }
}
