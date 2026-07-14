package com.serviteca.cliente.service;

import com.serviteca.cliente.dto.ClienteRequest;
import com.serviteca.cliente.dto.ClienteResponse;
import com.serviteca.cliente.entity.Cliente;
import com.serviteca.cliente.mapper.ClienteMapper;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.cliente.validator.ClienteValidator;
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
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ClienteValidator clienteValidator;

    public ClienteService(ClienteRepository clienteRepository, ClienteMapper clienteMapper,
                          ClienteValidator clienteValidator) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.clienteValidator = clienteValidator;
    }

    public PagedResponse<ClienteResponse> findAll(int page, int size, String sort, String search,
                                                  String filtro) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, sort));
        Page<Cliente> clientes;

        if (search != null && !search.isBlank()) {
            clientes = clienteRepository.search(search, pageRequest);
        } else if (filtro != null) {
            switch (filtro) {
                case "inactivos" -> clientes = clienteRepository.findByActivo(false, pageRequest);
                case "vip" -> clientes = clienteRepository.findVip(pageRequest);
                case "frecuentes" -> clientes = clienteRepository.findFrecuentes(5, pageRequest);
                case "nuevos" -> clientes = clienteRepository.findNuevos(LocalDateTime.now().minusMonths(1), pageRequest);
                default -> clientes = clienteRepository.findByActivoTrue(pageRequest);
            }
        } else {
            clientes = clienteRepository.findByActivoTrue(pageRequest);
        }

        return toPagedResponse(clientes);
    }

    public List<ClienteResponse> findAllSimple() {
        return clienteRepository.findByActivoTrue(PageRequest.of(0, Integer.MAX_VALUE, Sort.by("nombre"))).stream()
                .map(clienteMapper::toResponse)
                .toList();
    }

    public ClienteResponse findById(Long id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        return clienteMapper.toResponse(cliente);
    }

    public ClienteResponse create(ClienteRequest request) {
        clienteValidator.validateCreate(request);
        Cliente cliente = new Cliente();
        clienteMapper.updateEntity(request, cliente);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toResponse(cliente);
    }

    public ClienteResponse update(Long id, ClienteRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        clienteValidator.validateUpdate(id, request);
        clienteMapper.updateEntity(request, cliente);
        cliente = clienteRepository.save(cliente);
        return clienteMapper.toResponse(cliente);
    }

    public void delete(Long id, String motivo) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", id));
        cliente.marcarComoEliminado(obtenerUsuarioActual(), motivo);
        clienteRepository.save(cliente);
    }

    private String obtenerUsuarioActual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null ? auth.getName() : "SISTEMA";
    }

    private PagedResponse<ClienteResponse> toPagedResponse(Page<Cliente> page) {
        return new PagedResponse<>(
                page.getContent().stream().map(clienteMapper::toResponse).toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }
}
