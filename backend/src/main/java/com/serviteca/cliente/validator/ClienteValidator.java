package com.serviteca.cliente.validator;

import com.serviteca.cliente.dto.ClienteRequest;
import com.serviteca.cliente.repository.ClienteRepository;
import com.serviteca.shared.exception.DuplicateResourceException;
import org.springframework.stereotype.Component;

@Component
public class ClienteValidator {

    private final ClienteRepository clienteRepository;

    public ClienteValidator(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public void validateCreate(ClienteRequest request) {
        if (clienteRepository.existsByNumeroDocumento(request.getNumeroDocumento())) {
            throw new DuplicateResourceException(
                    "Ya existe un cliente con el documento " + request.getNumeroDocumento());
        }
    }

    public void validateUpdate(Long id, ClienteRequest request) {
        clienteRepository.findByNumeroDocumento(request.getNumeroDocumento())
                .ifPresent(existing -> {
                    if (!existing.getId().equals(id)) {
                        throw new DuplicateResourceException(
                                "Ya existe otro cliente con el documento " + request.getNumeroDocumento());
                    }
                });
    }
}
