package com.serviteca.shared.exception;

import com.serviteca.shared.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex,
                                                                     HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error(ex.getMessage());
        body.setStatus(HttpStatus.NOT_FOUND.value());
        body.setError(HttpStatus.NOT_FOUND.getReasonPhrase());
        body.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex,
                                                               HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error(ex.getMessage());
        body.setStatus(HttpStatus.BAD_REQUEST.value());
        body.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        body.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(DuplicateResourceException ex,
                                                                      HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error(ex.getMessage());
        body.setStatus(HttpStatus.CONFLICT.value());
        body.setError(HttpStatus.CONFLICT.getReasonPhrase());
        body.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex,
                                                                   HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error("Credenciales inv\u00e1lidas");
        body.setStatus(HttpStatus.UNAUTHORIZED.value());
        body.setError(HttpStatus.UNAUTHORIZED.getReasonPhrase());
        body.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex,
                                                                 HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error("Acceso denegado");
        body.setStatus(HttpStatus.FORBIDDEN.value());
        body.setError(HttpStatus.FORBIDDEN.getReasonPhrase());
        body.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });
        ApiResponse<Map<String, String>> response = ApiResponse.error("Error de validaci\u00f3n");
        response.setData(errors);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setError(HttpStatus.BAD_REQUEST.getReasonPhrase());
        response.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGeneral(Exception ex, HttpServletRequest request) {
        ApiResponse<Void> body = ApiResponse.error("Error interno del servidor");
        body.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.setError(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
        body.setPath(request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
