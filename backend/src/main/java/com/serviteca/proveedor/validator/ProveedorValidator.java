package com.serviteca.proveedor.validator;

import com.serviteca.proveedor.repository.ProveedorRepository;
import com.serviteca.shared.exception.DuplicateResourceException;
import org.springframework.stereotype.Component;

@Component
public class ProveedorValidator {

    private final ProveedorRepository proveedorRepository;

    public ProveedorValidator(ProveedorRepository proveedorRepository) {
        this.proveedorRepository = proveedorRepository;
    }

    public void validateCreate(String nit) {
        if (proveedorRepository.existsByNit(nit)) {
            throw new DuplicateResourceException("Ya existe un proveedor con el NIT " + nit);
        }
    }

    public void validateUpdate(Long id, String nit) {
        proveedorRepository.findByNit(nit).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateResourceException("Ya existe otro proveedor con el NIT " + nit);
            }
        });
    }
}
