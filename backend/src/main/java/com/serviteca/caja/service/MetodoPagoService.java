package com.serviteca.caja.service;

import com.serviteca.caja.dto.MetodoPagoRequest;
import com.serviteca.caja.dto.MetodoPagoResponse;
import com.serviteca.caja.entity.MetodoPago;
import com.serviteca.caja.repository.MetodoPagoRepository;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MetodoPagoService {

    private final MetodoPagoRepository repository;

    public MetodoPagoService(MetodoPagoRepository repository) {
        this.repository = repository;
    }

    public List<MetodoPagoResponse> listar() {
        return repository.findByActivoTrue().stream().map(this::toResponse).toList();
    }

    public List<MetodoPagoResponse> listarTodos() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    public MetodoPagoResponse crear(MetodoPagoRequest request) {
        MetodoPago mp = new MetodoPago();
        mp.setNombre(request.getNombre());
        mp.setActivo(true);
        return toResponse(repository.save(mp));
    }

    public MetodoPagoResponse actualizar(Long id, MetodoPagoRequest request) {
        MetodoPago mp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("M\u00e9todo de pago", id));
        mp.setNombre(request.getNombre());
        return toResponse(repository.save(mp));
    }

    public void toggleActivo(Long id) {
        MetodoPago mp = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("M\u00e9todo de pago", id));
        mp.setActivo(!mp.isActivo());
        repository.save(mp);
    }

    private MetodoPagoResponse toResponse(MetodoPago mp) {
        MetodoPagoResponse r = new MetodoPagoResponse();
        r.setId(mp.getId());
        r.setNombre(mp.getNombre());
        r.setActivo(mp.isActivo());
        return r;
    }
}
