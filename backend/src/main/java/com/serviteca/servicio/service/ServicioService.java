package com.serviteca.servicio.service;

import com.serviteca.categoria.entity.Categoria;
import com.serviteca.categoria.repository.CategoriaRepository;
import com.serviteca.servicio.dto.ServicioRequest;
import com.serviteca.servicio.dto.ServicioResponse;
import com.serviteca.servicio.entity.Servicio;
import com.serviteca.servicio.mapper.ServicioMapper;
import com.serviteca.servicio.repository.ServicioRepository;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final CategoriaRepository categoriaRepository;
    private final ServicioMapper servicioMapper;

    public ServicioService(ServicioRepository servicioRepository, CategoriaRepository categoriaRepository,
                           ServicioMapper servicioMapper) {
        this.servicioRepository = servicioRepository;
        this.categoriaRepository = categoriaRepository;
        this.servicioMapper = servicioMapper;
    }

    public PagedResponse<ServicioResponse> findAll(int page, int size, String sort, String search) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort != null ? sort : "nombre"));
        Page<Servicio> servicios;

        if (search != null && !search.isBlank()) {
            servicios = servicioRepository.search(search, pageRequest);
        } else {
            servicios = servicioRepository.findByActivoTrue(pageRequest);
        }

        return toPagedResponse(servicios);
    }

    public List<ServicioResponse> findAllSimple(String search) {
        List<Servicio> servicios;
        if (search != null && !search.isBlank()) {
            servicios = servicioRepository.search(search, Pageable.unpaged()).getContent();
        } else {
            servicios = servicioRepository.findAll(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("nombre"))).getContent();
        }
        return servicios.stream().map(servicioMapper::toResponse).toList();
    }

    public ServicioResponse findById(Long id) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", id));
        return servicioMapper.toResponse(servicio);
    }

    public ServicioResponse create(ServicioRequest request) {
        Servicio servicio = new Servicio();
        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionEstimadaMinutos(request.getDuracionEstimadaMinutos());

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categor\u00eda", request.getCategoriaId()));
            servicio.setCategoria(categoria);
        }

        servicio = servicioRepository.save(servicio);
        return servicioMapper.toResponse(servicio);
    }

    public ServicioResponse update(Long id, ServicioRequest request) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", id));

        servicio.setNombre(request.getNombre());
        servicio.setDescripcion(request.getDescripcion());
        servicio.setPrecio(request.getPrecio());
        servicio.setDuracionEstimadaMinutos(request.getDuracionEstimadaMinutos());

        if (request.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categor\u00eda", request.getCategoriaId()));
            servicio.setCategoria(categoria);
        } else {
            servicio.setCategoria(null);
        }

        servicio = servicioRepository.save(servicio);
        return servicioMapper.toResponse(servicio);
    }

    public void delete(Long id, String motivo) {
        Servicio servicio = servicioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", id));
        servicio.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        servicioRepository.save(servicio);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagedResponse<ServicioResponse> toPagedResponse(Page<Servicio> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(servicioMapper::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
