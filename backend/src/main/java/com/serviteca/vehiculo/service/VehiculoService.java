package com.serviteca.vehiculo.service;

import com.serviteca.cliente.entity.Cliente;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.DuplicateResourceException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import com.serviteca.vehiculo.dto.VehiculoRequest;
import com.serviteca.vehiculo.dto.VehiculoResponse;
import com.serviteca.vehiculo.entity.Vehiculo;
import com.serviteca.vehiculo.mapper.VehiculoMapper;
import com.serviteca.vehiculo.repository.VehiculoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final ClienteRepository clienteRepository;
    private final VehiculoMapper vehiculoMapper;

    public VehiculoService(VehiculoRepository vehiculoRepository, ClienteRepository clienteRepository,
                           VehiculoMapper vehiculoMapper) {
        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
        this.vehiculoMapper = vehiculoMapper;
    }

    public PagedResponse<VehiculoResponse> findAll(int page, int size, String sort, String search,
                                                   String marca, String modelo, Integer anio) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        Page<Vehiculo> vehiculos;

        if (search != null && !search.isBlank()) {
            vehiculos = vehiculoRepository.search(search, pageRequest);
        } else if (marca != null || modelo != null || anio != null) {
            vehiculos = vehiculoRepository.findByFilters(marca, modelo, anio, pageRequest);
        } else {
            vehiculos = vehiculoRepository.findAll(pageRequest);
        }

        return toPagedResponse(vehiculos);
    }

    public List<VehiculoResponse> findAllSimple() {
        return vehiculoRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("placa"))).stream()
                .map(vehiculoMapper::toResponse)
                .toList();
    }

    public VehiculoResponse findById(Long id) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", id));
        return vehiculoMapper.toResponse(vehiculo);
    }

    public List<VehiculoResponse> findByClienteId(Long clienteId) {
        return vehiculoRepository.findByClienteId(clienteId).stream()
                .map(vehiculoMapper::toResponse)
                .toList();
    }

    public VehiculoResponse create(VehiculoRequest request) {
        if (vehiculoRepository.existsByPlaca(request.getPlaca())) {
            throw new DuplicateResourceException("Ya existe un veh\u00edculo con la placa " + request.getPlaca());
        }

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClienteId()));

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setPlaca(request.getPlaca().toUpperCase());
        vehiculo.setMarca(request.getMarca());
        vehiculo.setLinea(request.getLinea());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setColor(request.getColor());
        vehiculo.setCilindraje(request.getCilindraje());
        vehiculo.setTipoVehiculo(request.getTipoVehiculo());
        vehiculo.setMotor(request.getMotor());
        vehiculo.setCombustible(request.getCombustible());
        vehiculo.setVin(request.getVin());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setCliente(cliente);

        vehiculo = vehiculoRepository.save(vehiculo);
        return vehiculoMapper.toResponse(vehiculo);
    }

    public VehiculoResponse update(Long id, VehiculoRequest request) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", id));

        vehiculoRepository.findByPlaca(request.getPlaca())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException(
                                "Ya existe otro veh\u00edculo con la placa " + request.getPlaca());
                    }
                });

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", request.getClienteId()));

        vehiculo.setPlaca(request.getPlaca().toUpperCase());
        vehiculo.setMarca(request.getMarca());
        vehiculo.setLinea(request.getLinea());
        vehiculo.setModelo(request.getModelo());
        vehiculo.setColor(request.getColor());
        vehiculo.setCilindraje(request.getCilindraje());
        vehiculo.setTipoVehiculo(request.getTipoVehiculo());
        vehiculo.setMotor(request.getMotor());
        vehiculo.setCombustible(request.getCombustible());
        vehiculo.setVin(request.getVin());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setCliente(cliente);

        vehiculo = vehiculoRepository.save(vehiculo);
        return vehiculoMapper.toResponse(vehiculo);
    }

    public void delete(Long id, String motivo) {
        Vehiculo vehiculo = vehiculoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Veh\u00edculo", id));
        vehiculo.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        vehiculoRepository.save(vehiculo);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagedResponse<VehiculoResponse> toPagedResponse(Page<Vehiculo> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(vehiculoMapper::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
