package com.serviteca.categoria.service;

import com.serviteca.categoria.dto.CategoriaRequest;
import com.serviteca.categoria.dto.CategoriaResponse;
import com.serviteca.categoria.entity.Categoria;
import com.serviteca.categoria.mapper.CategoriaMapper;
import com.serviteca.categoria.repository.CategoriaRepository;
import com.serviteca.shared.dto.PagedResponse;
import com.serviteca.shared.exception.DuplicateResourceException;
import com.serviteca.shared.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaMapper categoriaMapper;

    public CategoriaService(CategoriaRepository categoriaRepository, CategoriaMapper categoriaMapper) {
        this.categoriaRepository = categoriaRepository;
        this.categoriaMapper = categoriaMapper;
    }

    public PagedResponse<CategoriaResponse> findAll(int page, int size, String sort) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort != null ? sort : "nombre"));
        Page<Categoria> categorias = categoriaRepository.findByActivoTrue(pageRequest);
        return toPagedResponse(categorias);
    }

    public List<CategoriaResponse> findAllSimple() {
        return categoriaRepository.findByActivoTrue(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("nombre"))).stream()
                .map(categoriaMapper::toResponse)
                .toList();
    }

    public CategoriaResponse findById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categor\u00eda", id));
        return categoriaMapper.toResponse(categoria);
    }

    public CategoriaResponse create(CategoriaRequest request) {
        if (categoriaRepository.existsByNombre(request.getNombre())) {
            throw new DuplicateResourceException("La categor\u00eda '" + request.getNombre() + "' ya existe");
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }

    public CategoriaResponse update(Long id, CategoriaRequest request) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categor\u00eda", id));
        categoria.setNombre(request.getNombre());
        categoria.setDescripcion(request.getDescripcion());
        categoria = categoriaRepository.save(categoria);
        return categoriaMapper.toResponse(categoria);
    }

    public void delete(Long id, String motivo) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categor\u00eda", id));
        categoria.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        categoriaRepository.save(categoria);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagedResponse<CategoriaResponse> toPagedResponse(Page<Categoria> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(categoriaMapper::toResponse).toList(),
                page.getNumber(), page.getSize(), page.getTotalElements(),
                page.getTotalPages(), page.isLast()
        );
    }
}
