package com.serviteca.shared.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resource, Long id) {
        super(resource + " no encontrado con id: " + id);
    }

    public ResourceNotFoundException(String resource, String field, String value) {
        super(resource + " no encontrado con " + field + ": " + value);
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
