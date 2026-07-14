package com.serviteca.proveedor.service;

import com.serviteca.proveedor.dto.ProveedorRequest;
import com.serviteca.proveedor.dto.ProveedorResponse;
import com.serviteca.proveedor.entity.Proveedor;
import com.serviteca.proveedor.mapper.ProveedorMapper;
import com.serviteca.proveedor.repository.ProveedorRepository;
import com.serviteca.proveedor.validator.ProveedorValidator;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ProveedorMapper proveedorMapper;
    private final ProveedorValidator proveedorValidator;

    public ProveedorService(ProveedorRepository proveedorRepository, ProveedorMapper proveedorMapper,
                            ProveedorValidator proveedorValidator) {
        this.proveedorRepository = proveedorRepository;
        this.proveedorMapper = proveedorMapper;
        this.proveedorValidator = proveedorValidator;
    }

    public PagedResponse<ProveedorResponse> findAll(int page, int size, String sort, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort != null ? sort : "nombre"));
        Page<Proveedor> proveedores;

        if (search != null && !search.isBlank()) {
            proveedores = proveedorRepository.search(search, pageRequest);
        } else {
            proveedores = proveedorRepository.findByActivoTrue(pageRequest);
        }

        return toPagedResponse(proveedores);
    }

    public List<ProveedorResponse> findAllSimple(String search) {
        List<Proveedor> proveedores;
        if (search != null && !search.isBlank()) {
            proveedores = proveedorRepository.search(search, PageRequest.of(0, Integer.MAX_VALUE, Sort.by("nombre"))).getContent();
        } else {
            proveedores = proveedorRepository.findByActivoTrue(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("nombre"))).getContent();
        }
        return proveedores.stream().map(proveedorMapper::toResponse).toList();
    }

    public ProveedorResponse findById(Long id) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        return proveedorMapper.toResponse(proveedor);
    }

    public ProveedorResponse create(ProveedorRequest request) {
        proveedorValidator.validateCreate(request.getNit());
        Proveedor proveedor = new Proveedor();
        proveedor.setNit(request.getNit());
        proveedor.setNombre(request.getNombre());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        proveedor.setDireccion(request.getDireccion());
        proveedor = proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    public ProveedorResponse update(Long id, ProveedorRequest request) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        proveedorValidator.validateUpdate(id, request.getNit());
        proveedor.setNit(request.getNit());
        proveedor.setNombre(request.getNombre());
        proveedor.setContacto(request.getContacto());
        proveedor.setTelefono(request.getTelefono());
        proveedor.setEmail(request.getEmail());
        proveedor.setDireccion(request.getDireccion());
        proveedor = proveedorRepository.save(proveedor);
        return proveedorMapper.toResponse(proveedor);
    }

    public void delete(Long id, String motivo) {
        Proveedor proveedor = proveedorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor", id));
        proveedor.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        proveedorRepository.save(proveedor);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagedResponse<ProveedorResponse> toPagedResponse(Page<Proveedor> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(proveedorMapper::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
